package br.com.vcruz.stock.model;

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
public class Product extends Audit {

    private Long id;
    private String code;
    private String name;
    private String model;
    private String brand;
    private BigDecimal price;
    private boolean isDeleted;
    private User creatorUser;
}
