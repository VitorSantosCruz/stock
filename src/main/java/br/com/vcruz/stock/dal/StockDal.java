package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.Stock;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vcruz
 */
public interface StockDal {

    List<Long> save(int quantity, String size, String color, Long productId, Long createdBy);

    List<Stock> findAll(int quantity, int page);

    List<Stock> findAllExcept(List<Map<String, String>> cart, int quantity, int page);

    List<Stock> findAllOnCart(List<Map<String, String>> cart, int quantity, int page);
    
    List<Stock> findAllBySaleId(Long saleId);

    List<Stock> findBy(Map<String, String> featureMap, int quantity, int page);

    List<Stock> findByExcept(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page);

    List<Stock> findByOnCart(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page);

    void deleteAllByProductCode(String productCode);

    void deleteBy(int quantity, String size, String color, String productCode);

    int pageQuantity(int numberOfItemsPerPage, Map<String, String> featureMap);

    int pageQuantityExcept(List<Map<String, String>> cart, int numberOfItemsPerPage, Map<String, String> featureMap);

    int pageQuantityOnCart(List<Map<String, String>> cart, int numberOfItemsPerPage, Map<String, String> featureMap);
}
