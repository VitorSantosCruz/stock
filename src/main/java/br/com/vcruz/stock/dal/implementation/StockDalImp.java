package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.ProductInfoDal;
import br.com.vcruz.stock.dal.StockDal;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.Product;
import br.com.vcruz.stock.model.ProductInfo;
import br.com.vcruz.stock.model.Stock;
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

            ProductInfoDal productInfoDal = new ProductInfoDalImp();
            Long productInfoId = productInfoDal.save(connection, size, color);

            for (int i = 0; i < quantity; i++) {
                try {
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
        String query = "select *, count(*) stockQuantity from stock join product join product_info join user where stock.product_id = product.id and stock.product_info_id = product_info.id and stock.created_by = user.id and stock.sale_id is null and stock.is_deleted = false group by product.id, product_info.size, product_info.color limit ? offset ?";

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
    public List<Stock> findAllExcept(List<Map<String, String>> cart, int quantity, int page) {
        String query = """
                       select *, count(*) stockQuantity from stock join product join product_info 
                            where product.id = stock.product_id and product_info.id = stock.product_info_id and stock.is_deleted = false and stock.sale_id is null and
                            stock.id not in (select id from (
                       """;

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += """
                    ) temporary_table)
                    group by product.id, product_info.size, product_info.color limit ? offset ?;
                """;

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
            }

            preparedStatement.setInt(++i, quantity);
            preparedStatement.setInt(++i, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findAllExcept] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Stock> findAllOnCart(List<Map<String, String>> cart, int quantity, int page) {
        String query = """
                       select *, count(*) stockQuantity from stock join product join product_info 
                            where product.id = stock.product_id and product_info.id = stock.product_info_id and stock.is_deleted = false and stock.sale_id is null and
                            stock.id in (select id from (
                       """;

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += """
                    ) temporary_table)
                    group by product.id, product_info.size, product_info.color limit ? offset ?;
                """;

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
            }

            preparedStatement.setInt(++i, quantity);
            preparedStatement.setInt(++i, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findAllExcept] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<Stock> findBy(Map<String, String> featureMap, int quantity, int page) {
        String query = "select *, count(*) stockQuantity from stock join product join product_info join user where stock.product_id = product.id and stock.product_info_id = product_info.id and stock.created_by = user.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.sale_id is null and stock.is_deleted = false group by product.id, product_info.size, product_info.color limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
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
    public List<Stock> findByExcept(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page) {
        String query = "select *, count(*) stockQuantity from stock join product join product_info join user where stock.product_id = product.id and stock.product_info_id = product_info.id and stock.created_by = user.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.sale_id is null and stock.is_deleted = false and stock.id not in (select id from (";

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += ") temporary_table) group by product.id, product_info.size, product_info.color limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
            }

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
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
    public List<Stock> findByOnCart(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page) {
        String query = "select *, count(*) stockQuantity from stock join product join product_info join user where stock.product_id = product.id and stock.product_info_id = product_info.id and stock.created_by = user.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.sale_id is null and stock.is_deleted = false and stock.id in (select id from (";

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += ") temporary_table) group by product.id, product_info.size, product_info.color limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
            }

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
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
        String query = "select * from stock where id = ? and sale_id is null and is_deleted = false";

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
        String query = "update stock join product join product_info set stock.last_modified_date = now(), stock.is_deleted = true where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = product_info.id and product_info.size = ? and product_info.color = ? and product.product_code = ? and stock.is_deleted = false limit ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, size);
            preparedStatement.setString(2, color);
            preparedStatement.setString(3, productCode);
            preparedStatement.setInt(4, quantity);

            preparedStatement.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("[deleteById] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public void deleteAllByProductCode(String productCode) {
        String query = "update stock s join product join product_info set stock.last_modified_date = now(), stock.is_deleted = true where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = pi.id and product.product_code = ? and stock.sale_id is null and stock.is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, productCode);
            preparedStatement.executeUpdate();

        } catch (IOException | SQLException e) {
            log.error("[deleteAllByProductCode] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public int pageQuantity(int quantity, Map<String, String> featureMap) {
        String query = "select ceiling(count(*) / ?) pageQuantity from (select stock.id pageQuantity from stock join product join product_info where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = product_info.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.is_deleted = false group by product.id, product_info.size, product_info.color) temporary_table";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;
            preparedStatement.setInt(++i, quantity);

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

    @Override
    public int pageQuantityExcept(List<Map<String, String>> cart, int quantity, Map<String, String> featureMap) {
        String query = "select ceiling(count(*) / ?) pageQuantity from (select stock.id from stock join product join product_info where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = product_info.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.is_deleted = false and stock.id not in (select id from (";

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += ") temporary_table) group by product.id, product_info.size, product_info.color) temporary_table";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;
            preparedStatement.setInt(++i, quantity);

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
            }

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
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

    @Override
    public int pageQuantityOnCart(List<Map<String, String>> cart, int quantity, Map<String, String> featureMap) {
        String query = "select ceiling(count(*) / ?) pageQuantity from (select stock.id from stock join product join product_info where stock.sale_id is null and stock.product_id = product.id and stock.product_info_id = product_info.id and (";

        for (int i = 0; i < featureMap.keySet().size(); i++) {
            String key = (String) featureMap.keySet().toArray()[i];
            if (key.equals("size") || key.equals("color")) {
                query += "product_info." + key + " like ?";
            } else {
                query += "product." + key + " like ?";
            }

            if (i + 1 < featureMap.keySet().size()) {
                query += " and ";
            }
        }

        query += ") and stock.is_deleted = false and stock.id in (select id from (";

        for (int i = 0; i < cart.size(); i++) {
            query += """
                        (
                            select stock.id from stock join product join product_info
                                where product.id = stock.product_id and
                                product_info.id = stock.product_info_id and
                                stock.is_deleted = false and
                                stock.sale_id is null and
                                product.product_code = ? and
                                product_info.size = ? and
                                product_info.color = ? limit ?
                        )
                    """;

            if (i + 1 < cart.size()) {
                query += " union ";
            }
        }

        query += ") temporary_table) group by product.id, product_info.size, product_info.color) temporary_table";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            int i = 0;
            preparedStatement.setInt(++i, quantity);

            for (String value : featureMap.values()) {
                preparedStatement.setString(++i, "%" + value + "%");
            }

            for (Map<String, String> product : cart) {
                preparedStatement.setString(++i, product.get("productCode"));
                preparedStatement.setString(++i, product.get("size"));
                preparedStatement.setString(++i, product.get("color"));
                preparedStatement.setInt(++i, Integer.parseInt(product.get("quantity")));
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
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<Stock> stocks = new ArrayList<>();

            while (resultSet.next()) {
                stocks.add(StockDalImp.getStockFromResultSet(resultSet));
            }

            return stocks;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static Stock getStockFromResultSet(ResultSet resultSet) throws SQLException {
        LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("stock.created_Date"));
        LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("stock.last_modified_date"));
        Long id = resultSet.getLong("stock.id");
        boolean isDeleted = resultSet.getBoolean("stock.is_deleted");
        int quantity = resultSet.getInt("stockQuantity");

        Product product = ProductDalImp.getProductFromResultSet(resultSet);
        ProductInfo productInfo = ProductInfoDalImp.getProductInfoFromResultSet(resultSet);

        return Stock.builder()
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .id(id)
                .isDeleted(isDeleted)
                .product(product)
                .productInfo(productInfo)
                .quantity(quantity)
                .build();
    }
}
