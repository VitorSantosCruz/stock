package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.LoginDal;
import br.com.vcruz.stock.dal.implementation.LoginDalImp;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.PasswordUtils;
import java.security.NoSuchAlgorithmException;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class LoginService {

    private final LoginDal loginDal;

    public LoginService() {
        this.loginDal = new LoginDalImp();
    }

    public User login(String login, String password) {
        log.info("[login] - Fazendo login com o usuário: {}", login);

        try {
            return this.loginDal.login(login, PasswordUtils.encryptPassword(password));
        } catch (NoSuchAlgorithmException e) {
            log.error("[login] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }
}
