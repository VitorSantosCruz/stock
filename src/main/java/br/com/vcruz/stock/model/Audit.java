package br.com.vcruz.stock.model;

import java.time.LocalDateTime;
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
public abstract class Audit {

    protected LocalDateTime createdDate;
    protected LocalDateTime lastModifiedDate;
}
