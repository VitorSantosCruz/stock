package br.com.vcruz.stock.model;

import java.time.LocalDateTime;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author vcruz
 */
@SuperBuilder
public abstract class Audit {

    protected LocalDateTime createdDate;
    protected LocalDateTime lastModifiedDate;
}
