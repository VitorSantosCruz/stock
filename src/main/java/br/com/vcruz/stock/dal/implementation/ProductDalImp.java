package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.ProductDal;
import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.exception.DuplicateException;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class ProductDalImp implements ProductDal {

    @Override
    public Product save(String code, String name, String model, String brand, BigDecimal price, Long createdBy) {
        String query = "insert into product (product_code, name, model, brand, price, created_by) values (?, ?, ?, ?, ?, ?)";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, model);
            preparedStatement.setString(4, brand);
            preparedStatement.setBigDecimal(5, price);
            preparedStatement.setLong(6, createdBy);

            preparedStatement.executeUpdate();

            return this.findByCode(code);
        } catch (IOException | SQLException e) {
            log.error("[save] - {}", e.getMessage());

            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateException("O código escolhido não está disponível, tente outro.");
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public Product update(Long id, String code, String name, String model, String brand, BigDecimal price, Long createdBy) {
        String query = "update product set last_modified_date = now(), product_code = ?, name = ?, model = ?, brand = ?, price = ?, created_by = ? where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, model);
            preparedStatement.setString(4, brand);
            preparedStatement.setBigDecimal(5, price);
            preparedStatement.setLong(6, createdBy);
            preparedStatement.setLong(7, id);

            preparedStatement.executeUpdate();

            return this.findByCode(code);
        } catch (IOException | SQLException e) {
            log.error("[update] - {}", e.getMessage());

            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateException("O código escolhido não está disponível, tente outro.");
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Product> findAll(int quantity, int page) {
        String query = "select * from product p join user u where p.is_deleted = false group by p.id order by p.id asc limit ? offset ?";

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
    public List<Product> findBy(Map<String, String> featureMap, int quantity, int page) {
        String query = "select * from product p join user u where (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            query += "p." + key + " like ?";

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and p.is_deleted = false group by p.id order by p.id asc limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            int i = 0;

            for (String value : featureMap.values()) {
                i++;
                preparedStatement.setString(i, "%" + value + "%");
            }

            preparedStatement.setInt(++i, quantity);
            preparedStatement.setInt(++i, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findBy] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public Product findByCode(String code) {
        String query = "select * from product p join user u where p.product_code = ? and p.is_deleted = false group by p.id order by p.id asc";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);

            List<Product> productList = this.getResult(preparedStatement);

            if (productList.isEmpty()) {
                String errorMessage = "Esse produto não existe!";
                throw new NotFoundException(errorMessage);
            }

            return productList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findByCode] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        String query = "update product set last_modified_date = now(), is_deleted = true where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("[deleteById] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public int pageQuantity(int quantity, Map<String, String> featureMap) {
        String query = "select ceiling(count(*) / ?) as pageQuantity from product where (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            query += key + " like ?";

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query.toString())) {
            preparedStatement.setInt(1, quantity);

            for (int i = 0; i < featureMap.values().size(); i++) {
                String value = (String) featureMap.values().toArray()[i];

                preparedStatement.setString(i + 2, "%" + value + "%");
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

    private List<Product> getResult(PreparedStatement preparedStatement) throws NotFoundException, SQLException {
        UserDal userDal = new UserDalImp();
        List<Product> products = new ArrayList<>();

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("created_Date"));
                LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("last_modified_date"));
                Long id = resultSet.getLong("p.id");
                String code = resultSet.getString("p.product_code");
                String name = resultSet.getString("p.name");
                String model = resultSet.getString("p.model");
                String brand = resultSet.getString("p.brand");
                BigDecimal price = resultSet.getBigDecimal("p.price");
                boolean isDeleted = resultSet.getBoolean("p.is_deleted");

                String creatorUserLogin = resultSet.getString("u.login");
                User creatorUser = userDal.findByLogin(creatorUserLogin);

                Product product = Product.builder()
                        .createdDate(createdDate)
                        .lastModifiedDate(lastModifiedDate)
                        .id(id)
                        .code(code)
                        .name(name)
                        .model(model)
                        .brand(brand)
                        .price(price)
                        .isDeleted(isDeleted)
                        .creatorUser(creatorUser)
                        .build();
                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            throw e;
        }
    }
}
