package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.SaleDal;
import br.com.vcruz.stock.dal.implementation.SaleDalImp;
import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.model.Sale;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class SaleService {

    private final SaleDal saleDal;

    public SaleService() {
        this.saleDal = new SaleDalImp();
    }

    public Sale save(List<Map<String, String>> cart, BigDecimal price, PaymentMethod formOfPayment, BigDecimal discount, Long createdBy) {
        log.info("[save] - Vendendo os produtos: {}", cart);

        return this.saleDal.save(cart, price, formOfPayment, discount, createdBy);
    }

    public List<Sale> findAll(LocalDateTime startDate, LocalDateTime endDate, int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "venda" : "vendas") + " da página {}", quantity, page);

        return this.saleDal.findAll(startDate, endDate, quantity, page);
    }

    public Sale findById(Long id) {
        log.info("Procurando a venda pelo id: {}.", id);

        return this.saleDal.findById(id);
    }
}
