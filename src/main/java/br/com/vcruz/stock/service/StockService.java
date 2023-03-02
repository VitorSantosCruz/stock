package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.StockDal;
import br.com.vcruz.stock.dal.implementation.StockDalImp;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.ValidationException;
import br.com.vcruz.stock.model.Stock;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class StockService {

    private final StockDal stockDal;

    public StockService() {
        this.stockDal = new StockDalImp();
    }

    public List<Long> save(int quantity, String size, String color, Long productId, Long createdBy) {
        log.info("[save] - Cadastrando {} " + (quantity == 1 ? "produto" : "produtos") + " com o tamanho {} e a cor {} no estoque", quantity, size, color);

        this.StockValidaion(quantity, size, color);

        return this.stockDal.save(quantity, size, color, productId, createdBy);
    }

    public List<Stock> findAll(int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "produto" : "produtos") + " da página {}", quantity, page);

        return this.stockDal.findAll(quantity, page);
    }

    public List<Stock> findAllExcept(List<Map<String, String>> cart, int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "produto" : "produtos") + " da página {} exceto {}", quantity, page, cart);

        return this.stockDal.findAllExcept(cart, quantity, page);
    }

    public List<Stock> findAllOnCart(List<Map<String, String>> cart, int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "produto" : "produtos") + " da página {} que correspondam a {}", quantity, page, cart);

        return this.stockDal.findAllOnCart(cart, quantity, page);
    }

    public List<Stock> findAllBySaleId(Long saleId) {
        log.info("[findAll] - obtendo todos os estoques relativos a venda {}", saleId);

        return this.stockDal.findAllBySaleId(saleId);
    }

    public List<Stock> findBy(Map<String, String> featureMap, int quantity, int page) {
        log.info("[findBy] - procurando {} produtos que tenha algum atribuo que corresponda a '{}' na página {}", quantity, featureMap, page);

        return this.stockDal.findBy(featureMap, quantity, page);
    }

    public List<Stock> findByExcept(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page) {
        log.info("[findBy] - procurando {} produtos que tenha algum atribuo que corresponda a '{}' na página {} exceto {}", quantity, featureMap, page, cart);

        return this.stockDal.findByExcept(cart, featureMap, quantity, page);
    }

    public List<Stock> findByOnCart(List<Map<String, String>> cart, Map<String, String> featureMap, int quantity, int page) {
        log.info("[findBy] - procurando {} produtos que tenha algum atribuo que corresponda a '{}' na página {} que correspondam a {}", quantity, featureMap, page, cart);

        return this.stockDal.findByOnCart(cart, featureMap, quantity, page);
    }

    public void deleteBy(int quantity, String size, String color, String productCode) {
        log.info("[deleteBy] - apagando {} " + (quantity == 1 ? "produto" : "produtos") + " com o código {} do estoque.", quantity, productCode);

        this.stockDal.deleteBy(quantity, size, color, productCode);
    }

    public int pageQuantity(int numberOfItemsPerPage) {
        Map<String, String> featureMap = Map.of("name", "");

        return this.pageQuantity(numberOfItemsPerPage, featureMap);
    }

    public int pageQuantity(int numberOfItemsPerPage, Map<String, String> featureMap) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (numberOfItemsPerPage == 1 ? "item" : "itens cada."), numberOfItemsPerPage);

        try {
            return this.stockDal.pageQuantity(numberOfItemsPerPage, featureMap);
        } catch (InternalException e) {
            return 0;
        }
    }

    public int pageQuantityExcept(List<Map<String, String>> cart, int numberOfItemsPerPage) {
        Map<String, String> featureMap = Map.of("name", "");

        return this.pageQuantityExcept(cart, numberOfItemsPerPage, featureMap);
    }

    public int pageQuantityExcept(List<Map<String, String>> cart, int numberOfItemsPerPage, Map<String, String> featureMap) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (numberOfItemsPerPage == 1 ? "item" : "itens cada, exceto {}"), numberOfItemsPerPage, cart);

        try {
            return this.stockDal.pageQuantityExcept(cart, numberOfItemsPerPage, featureMap);
        } catch (InternalException e) {
            return 0;
        }
    }

    public int pageQuantityOnCart(List<Map<String, String>> cart, int numberOfItemsPerPage) {
        Map<String, String> featureMap = Map.of("name", "");

        return this.pageQuantityOnCart(cart, numberOfItemsPerPage, featureMap);
    }

    public int pageQuantityOnCart(List<Map<String, String>> cart, int numberOfItemsPerPage, Map<String, String> featureMap) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (numberOfItemsPerPage == 1 ? "item" : "itens cada, que corresponda a {}"), numberOfItemsPerPage, cart);

        try {
            return this.stockDal.pageQuantityOnCart(cart, numberOfItemsPerPage, featureMap);
        } catch (InternalException e) {
            return 0;
        }
    }

    private void quantityValidation(int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("A quantidade não pode ser menor ou igual a 0!");
        }
    }

    private void sizeValidation(String size) {
        if (size.isBlank()) {
            throw new ValidationException("O tamanho não pode ser vazio!");
        }
    }

    private void colorValidation(String color) {
        if (color.isBlank()) {
            throw new ValidationException("A cor não pode ser vazia!");
        }
    }

    private void StockValidaion(int quantity, String size, String color) {
        this.quantityValidation(quantity);
        this.sizeValidation(size);
        this.colorValidation(color);
    }
}
