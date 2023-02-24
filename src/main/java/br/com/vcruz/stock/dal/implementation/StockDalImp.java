package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.ProductDal;
import br.com.vcruz.stock.dal.ProductInfoDal;
import br.com.vcruz.stock.dal.SaleDal;
import br.com.vcruz.stock.dal.StockDal;
import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.model.ProductInfo;
import br.com.vcruz.stock.model.Sale;
import br.com.vcruz.stock.model.Stock;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class StockDalImp implements StockDal {

    @Override
    public List<Long> save(int quantity, String size, String color, Long productId, Long createdBy) {
        String query = "insert into stock (product_id, product_info_id, created_by) values (?, ?, ?)";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            List<Long> stockIdList = new ArrayList<>();
            connection.setAutoCommit(false);

            for (int i = 0; i < quantity; i++) {
                try {
                    ProductInfoDal productInfoDal = new ProductInfoDalImp();
                    Long productInfoId = productInfoDal.save(connection, size, color);

                    preparedStatement.setLong(1, productId);
                    preparedStatement.setLong(2, productInfoId);
                    preparedStatement.setLong(3, createdBy);

                    preparedStatement.executeUpdate();

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        generatedKeys.next();
                        stockIdList.add(generatedKeys.getLong(1));
                    } catch (SQLException e) {
                        throw e;
                    }
                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }

            connection.commit();

            return stockIdList;
        } catch (IOException | SQLException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Stock> findAll(int quantity, int page) {
        String query = "select * from stock where is_deleted = false limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findAll] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Stock> findBy(String size, String color, Map<String, String> featureMap, int quantity, int page) {
        String query = "select * from stock s join product p where s.product_id = p.id and s.size like ? s.color like ? (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            query += "p." + key + " like ?";

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and s.is_deleted = false limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            int i = 0;
            preparedStatement.setString(++i, "%" + size + "%");
            preparedStatement.setString(++i, "%" + color + "%");

            for (String value : featureMap.values()) {
                i++;
                preparedStatement.setString(i, "%" + value + "%");
            }

            preparedStatement.setInt(++i, quantity);
            preparedStatement.setInt(++i, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findByProductFeatures] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public Stock findById(Long id) {
        String query = "select * from stock where id = ? and is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            List<Stock> stockList = this.getResult(preparedStatement);

            if (stockList.isEmpty()) {
                String errorMessage = "Esse produto não existe no estoque!";
                throw new NotFoundException(errorMessage);
            }

            return stockList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findById] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public void deleteBy(int quantity, String size, String color, String productCode) {
        String query = "update stock s join product p set s.last_modified_date = now(), s.is_deleted = true where s.product_id = p.id and s.size = ? and s.color = ? and p.product_code = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            for (int i = 0; i < quantity; i++) {
                try {
                    preparedStatement.setString(1, size);
                    preparedStatement.setString(2, color);
                    preparedStatement.setString(3, productCode);
                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    connection.rollback();
                    throw e;
                }
            }

            connection.commit();
        } catch (IOException | SQLException e) {
            log.error("[deleteById] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public int pageQuantity(int quantity, String size, String color, Map<String, String> featureMap) {
        String query = "select ceiling(count(*) / ?) as pageQuantity from stock s join product p where s.product_id = p.id and s.size like ? s.color like ? (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            query += "p." + key + " like ?";

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and s.is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            int i = 0;
            preparedStatement.setInt(++i, quantity);
            preparedStatement.setString(++i, "%" + size + "%");
            preparedStatement.setString(++i, "%" + color + "%");

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
            }

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

    private List<Stock> getResult(PreparedStatement preparedStatement) throws NotFoundException, SQLException {
        ProductDal productDal = new ProductDalImp();
        ProductInfoDal productInfoDal = new ProductInfoDalImp();
        SaleDal saleDal = new SaleDalImp();
        UserDal userDal = new UserDalImp();
        List<Stock> stocks = new ArrayList<>();

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("created_Date"));
                LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("last_modified_date"));
                Long id = resultSet.getLong("id");
                boolean isDeleted = resultSet.getBoolean("is_deleted");

                Long productId = resultSet.getLong("product_id");
                Long productInfoId = resultSet.getLong("product_info_id");
                Long saleId = resultSet.getLong("sele_id");
                Long creatorId = resultSet.getLong("created_by");

                Product product = productDal.findById(productId);
                ProductInfo productInfo = productInfoDal.findById(productInfoId);
                Sale sale = saleDal.findById(saleId);
                User creator = userDal.findById(creatorId);

                Stock stock = Stock.builder()
                        .createdDate(createdDate)
                        .lastModifiedDate(lastModifiedDate)
                        .id(id)
                        .isDeleted(isDeleted)
                        .product(product)
                        .productInfo(productInfo)
                        .sale(sale)
                        .creator(creator)
                        .build();
                stocks.add(stock);
            }

            return stocks;
        } catch (SQLException e) {
            throw e;
        }
    }
}
