package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.model.Sale;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 * @author vcruz
 */
public interface SaleDal {

    Sale save(List<Map<String, String>> cart, BigDecimal price, PaymentMethod formOfPayment, BigDecimal discount, Long createdBy);

    List<Sale> findAll(LocalDateTime startDate, LocalDateTime endDate, int quantity, int page);

    Sale findById(Long id);
}
