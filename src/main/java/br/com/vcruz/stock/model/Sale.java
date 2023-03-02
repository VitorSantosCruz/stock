package br.com.vcruz.stock.model;

import br.com.vcruz.stock.enumerator.PaymentMethod;
import br.com.vcruz.stock.service.StockService;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
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
    private List<Stock> stocks;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private final StockService stockService = new StockService();

    public List<Stock> getStocks() {
        if (this.stocks == null) {
            this.stocks = this.stockService.findAllBySaleId(this.id);
        }

        return this.stocks;
    }
}
