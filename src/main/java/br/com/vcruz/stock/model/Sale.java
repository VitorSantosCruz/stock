package br.com.vcruz.stock.model;

import br.com.vcruz.stock.enumerator.PaymentMethod;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author vcruz
 */
@Getter
@Setter
@SuperBuilder
public class Sale extends Audit {

    private Long id;
    private BigDecimal price;
    private PaymentMethod formOfPayment;
    private BigDecimal discount;
    private Stock stock;
}
