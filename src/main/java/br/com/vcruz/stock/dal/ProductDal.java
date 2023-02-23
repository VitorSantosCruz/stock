package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vcruz
 */
public interface ProductDal {

    Product save(String code, String name, String model, String brand, BigDecimal price, Long createdBy);

    Product update(Long id, String code, String name, String model, String brand, BigDecimal price, Long createdBy);

    List<Product> findAll(int quantity, int page);

    List<Product> findBy(Map<String, String> featureMap, int quantity, int page);

    Product findByCode(String code);

    void deleteById(Long id);

    int pageQuantity(int quantity, Map<String, String> featureMap);
}
