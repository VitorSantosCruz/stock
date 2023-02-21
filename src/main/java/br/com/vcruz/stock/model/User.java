package br.com.vcruz.stock.model;

import java.time.LocalTime;
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
public class User extends Audit {

    private Long id;
    private String name;
    private String login;
    private String password;
    private boolean isRoot;
    private int loginAttempCont;
    private LocalTime blockedUntil;
    private boolean isDeleted;

    public boolean isBlocked() {
        return this.blockedUntil != null
                && this.blockedUntil.isAfter(LocalTime.now());
    }
}
