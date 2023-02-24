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
public class ProductInfo extends Audit {

    private Long id;
    private String size;
    private String color;
}
