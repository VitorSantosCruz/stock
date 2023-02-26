package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.SaleDal;
import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.Sale;
import br.com.vcruz.stock.model.Stock;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class SaleDalImp implements SaleDal {

    @Override
    public Sale save(List<Map<String, String>> cart, BigDecimal price, PaymentMethod formOfPayment, BigDecimal discount, Long createdBy) {
        String querySale = "insert into sale (price, form_of_payment, discount, seller_id) values (?, ?, ?, ?)";
        String queryStockSaleIdUpdate = "update stock join product join product_info set stock.last_modified_date = now(), stock.sale_id = ? where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = product_info.id and product_info.size = ? and product_info.color = ? and product.product_code = ? and stock.is_deleted = false limit ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatementSale = connection.prepareStatement(querySale, Statement.RETURN_GENERATED_KEYS)) {
            Long saleId;
            connection.setAutoCommit(false);

            try {
                preparedStatementSale.setBigDecimal(1, price);
                preparedStatementSale.setString(2, formOfPayment.toString());
                preparedStatementSale.setBigDecimal(3, discount);
                preparedStatementSale.setLong(4, createdBy);

                preparedStatementSale.executeUpdate();

                try (ResultSet generatedKeys = preparedStatementSale.getGeneratedKeys()) {
                    generatedKeys.next();
                    saleId = generatedKeys.getLong(1);
                } catch (SQLException e) {
                    throw e;
                }

                try (PreparedStatement preparedStatementStockSaleIdUpdate = connection.prepareStatement(queryStockSaleIdUpdate)) {
                    connection.setAutoCommit(false);
                    for (Map<String, String> product : cart) {
                        int quantity = Integer.parseInt(product.get("quantity"));

                        preparedStatementStockSaleIdUpdate.setLong(1, saleId);
                        preparedStatementStockSaleIdUpdate.setString(2, product.get("size"));
                        preparedStatementStockSaleIdUpdate.setString(3, product.get("color"));
                        preparedStatementStockSaleIdUpdate.setString(4, product.get("productCode"));
                        preparedStatementStockSaleIdUpdate.setInt(5, quantity);

                        int affectedRows = preparedStatementStockSaleIdUpdate.executeUpdate();

                        if (affectedRows < quantity) {
                            throw new SQLException("O estoque não possui a quantidade suficiente de produtos!");
                        }
                    }
                } catch (SQLException e) {
                    throw e;
                }
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

            connection.commit();

            return this.findById(saleId);
        } catch (IOException | SQLException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Sale> findAll(LocalDate startDate, LocalDate endDate, int quantity, int page) {
        String query = "select *, 0 stockQuantity from sale join stock join product join product_info where sale.id = stock.sale_id and product.id = stock.product_id and product_info.id = stock.product_info_id and sale.created_date <= ? and sale.created_date >= ? group by sale.id limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, new Timestamp(DateConverterUtils.convertToDate(endDate.atTime(LocalTime.MAX)).getTime()));
            preparedStatement.setTimestamp(2, new Timestamp(DateConverterUtils.convertToDate(startDate.atTime(LocalTime.MIN)).getTime()));
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
        String query = "select *, 0 stockQuantity from sale join stock join product join product_info where sale.id = stock.sale_id and product.id = stock.product_id and product_info.id = stock.product_info_id and sale.id = ?";

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

    @Override
    public int pageQuantity(int numberOfItemsPerPage, LocalDate startDate, LocalDate endDate) {
        String query = "select ceiling(count(*) / ?) as pageQuantity from sale where sale.created_date <= ? and sale.created_date >= ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, numberOfItemsPerPage);
            preparedStatement.setTimestamp(2, new Timestamp(DateConverterUtils.convertToDate(endDate.atTime(LocalTime.MAX)).getTime()));
            preparedStatement.setTimestamp(3, new Timestamp(DateConverterUtils.convertToDate(startDate.atTime(LocalTime.MIN)).getTime()));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("pageQuantity");
            } catch (SQLException e) {
                throw e;
            }
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[pageQuantity] - {}", e.getMessage());

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

            Sale sale = Sale.builder()
                    .createdDate(createdDate)
                    .lastModifiedDate(lastModifiedDate)
                    .id(id)
                    .price(price)
                    .formOfPayment(paymentMethod)
                    .discount(discount)
                    .stock(stock)
                    .build();
            sales.add(sale);
        }

        return sales;
    }
}
