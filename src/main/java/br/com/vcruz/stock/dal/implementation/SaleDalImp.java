package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.SaleDal;
import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.Sale;
import br.com.vcruz.stock.model.Stock;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class SaleDalImp implements SaleDal {

    @Override
    public List<Long> save(int quantity, BigDecimal price, PaymentMethod formOfPayment, BigDecimal discount, Long createdBy) {
        String query = "insert into sale (price, formOfPayment, discount, createdBy) values (?, ?, ?, ?)";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            List<Long> saleIdList = new ArrayList<>();
            connection.setAutoCommit(false);

            for (int i = 0; i < quantity; i++) {
                try {
                    preparedStatement.setBigDecimal(1, price);
                    preparedStatement.setString(2, formOfPayment.toString());
                    preparedStatement.setBigDecimal(3, discount);
                    preparedStatement.setLong(4, createdBy);

                    preparedStatement.executeUpdate();

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        generatedKeys.next();
                        saleIdList.add(generatedKeys.getLong(1));
                    } catch (SQLException e) {
                        throw e;
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }

            connection.commit();
            return saleIdList;
        } catch (IOException | SQLException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Sale> findAll(LocalDateTime startDate, LocalDateTime endDate, int quantity, int page) {
        String query = "select * from sale join stock join user where sale.id = stock.sale_id and user.id = sale.seller_id and  sa.created_date < ? and sa.created_date > ? limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, new Timestamp(DateConverterUtils.convertToDate(startDate).getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(DateConverterUtils.convertToDate(endDate).getTime()));
            preparedStatement.setInt(3, quantity);
            preparedStatement.setInt(4, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findAll] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public Sale findById(Long id) {
        String query = "select * from sale join stock join user where sale.id = stock.sale_id and user.id = sale.seller_id and sale.id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            List<Sale> saleList = this.getResult(preparedStatement);

            if (saleList.isEmpty()) {
                String errorMessage = "Essa venda não existe!";
                throw new NotFoundException(errorMessage);
            }

            return saleList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findById] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    private List<Sale> getResult(PreparedStatement preparedStatement) throws SQLException {

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            return SaleDalImp.getSalesFromResultSet(resultSet, preparedStatement);
        } catch (SQLException e) {
            throw e;
        }
    }

    public static List<Sale> getSalesFromResultSet(ResultSet resultSet, PreparedStatement preparedStatement) throws SQLException {
        List<Sale> sales = new ArrayList<>();

        while (resultSet.next()) {
            LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("sale.created_Date"));
            LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("sale.last_modified_date"));
            Long id = resultSet.getLong("sale.id");
            BigDecimal price = resultSet.getBigDecimal("sale.price");
            PaymentMethod paymentMethod = PaymentMethod.valueOf(resultSet.getString("sale.form_of_payment"));
            BigDecimal discount = resultSet.getBigDecimal("sale.discount");

            Stock stock = StockDalImp.getStockFromResultSet(resultSet);
            User seller = UserDalImp.getUserFromResultSet(resultSet);

            Sale sale = Sale.builder()
                    .createdDate(createdDate)
                    .lastModifiedDate(lastModifiedDate)
                    .id(id)
                    .price(price)
                    .formOfPayment(paymentMethod)
                    .discount(discount)
                    .stock(stock)
                    .seller(seller)
                    .build();
            sales.add(sale);
        }

        return sales;
    }
}