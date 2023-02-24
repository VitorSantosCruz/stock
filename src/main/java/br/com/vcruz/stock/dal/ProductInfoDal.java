package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.ProductInfo;
import java.sql.Connection;

/**
 *
 * @author vcruz
 */
public interface ProductInfoDal {

    Long save(Connection connection, String size, String color);

    ProductInfo findById(Long id);
}
