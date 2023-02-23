package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.ProductDal;
import br.com.vcruz.stock.dal.implementation.ProductDalImp;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.ValidationException;
import br.com.vcruz.stock.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class ProductService {

    private final ProductDal productDal;

    public ProductService() {
        this.productDal = new ProductDalImp();
    }

    public Product save(String code, String name, String model, String brand, BigDecimal price, Long createdBy) {
        log.info("[save] - Cadastrando o produto: {}", code);

        this.ProductValidaion(code, name, model, brand, price);

        return this.productDal.save(code, name, model, brand, price, createdBy);
    }

    public Product update(Long id, String code, String name, String model, String brand, BigDecimal price, Long createdBy) {
        log.info("[update] - Editando o produto: {}", id);

        this.ProductValidaion(code, name, model, brand, price);

        return this.productDal.update(id, code, name, model, brand, price, createdBy);
    }

    public List<Product> findAll(int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "produto" : "produtos") + " da página {}", quantity, page);

        return this.productDal.findAll(quantity, page);
    }

    public List<Product> findBy(Map<String, String> featureMap, int quantity, int page) {
        log.info("[findBy] - procurando {} produtos que tenha algum atribuo que corresponda a '{}' na página {}", quantity, featureMap, page);

        return this.productDal.findBy(featureMap, quantity, page);
    }

    public Product findByCode(String code) {
        log.info("Procurando o produto pelo código {}.", code);

        return this.productDal.findByCode(code);
    }

    public void deleteById(Long id) {
        log.info("[delete] - apagando produto {}.", id);

        this.productDal.deleteById(id);
    }

    public int pageQuantity(int quantity) {
        Map<String, String> featureMap = Map.of("name", "");

        return this.pageQuantity(quantity, featureMap);
    }

    public int pageQuantity(int quantity, Map<String, String> featureMap) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (quantity == 1 ? "item" : "itens cada."), quantity);

        try {
            return this.productDal.pageQuantity(quantity, featureMap);
        } catch (InternalException e) {
            return 0;
        }
    }

    private void codeValidation(String code) {
        if (code.isBlank()) {
            throw new ValidationException("O código não pode ser vazio!");
        }
    }

    private void nameValidation(String name) {
        if (name.isBlank()) {
            throw new ValidationException("O nome não pode ser vazio!");
        }
    }

    private void modelValidation(String model) {
        if (model.isBlank()) {
            throw new ValidationException("O modelo não pode ser vazio!");
        }
    }

    private void brandValidation(String brand) {
        if (brand.isBlank()) {
            throw new ValidationException("A marca não pode ser vazia!");
        }
    }

    private void priceValidation(BigDecimal price) {
        if (price == null || BigDecimal.ZERO.equals(price)) {
            throw new ValidationException("O preço não pode ser 0!");
        }
    }

    private void ProductValidaion(String code, String name, String model, String brand, BigDecimal price) {
        this.codeValidation(code);
        this.nameValidation(name);
        this.modelValidation(model);
        this.brandValidation(brand);
        this.priceValidation(price);
    }
}
