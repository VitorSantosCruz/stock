package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.ProductInfoDal;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.ProductInfo;
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
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class ProductInfoDalImp implements ProductInfoDal {

    @Override
    public Long save(Connection connection, String size, String color) {
        String query = "insert into product_info (size, color) values (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, size);
            preparedStatement.setString(2, color);

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getLong(1);
            } catch (SQLException e) {
                throw e;
            }
        } catch (SQLException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public ProductInfo findById(Long id) {
        String query = "select * from product_info where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            List<ProductInfo> productInfoList = this.getResult(preparedStatement);

            if (productInfoList.isEmpty()) {
                String errorMessage = "Essa informação de produto não existe!";
                throw new NotFoundException(errorMessage);
            }

            return productInfoList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findById] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    private List<ProductInfo> getResult(PreparedStatement preparedStatement) throws SQLException {

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<ProductInfo> productsInfo = new ArrayList<>();

            while (resultSet.next()) {
                productsInfo.add(ProductInfoDalImp.getProductInfoFromResultSet(resultSet));
            }

            return productsInfo;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static ProductInfo getProductInfoFromResultSet(ResultSet resultSet) throws SQLException {
        LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("product_info.created_Date"));
        LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("product_info.last_modified_date"));
        Long id = resultSet.getLong("product_info.id");
        String size = resultSet.getString("product_info.size");
        String color = resultSet.getString("product_info.color");

        return ProductInfo.builder()
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .id(id)
                .size(size)
                .color(color)
                .build();
    }
}
