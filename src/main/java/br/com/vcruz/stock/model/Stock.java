package br.com.vcruz.stock.model;

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
public class Stock extends Audit {

    private Long id;
    private boolean isDeleted;
    private Product product;
    private ProductInfo productInfo;
    private Sale sale;
    private int quantity;
}
