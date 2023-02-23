package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.dal.implementation.UserDalImp;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.LoginException;
import br.com.vcruz.stock.exception.PasswordException;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.PasswordUtils;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class UserService {

    private final UserDal userDal;

    public UserService() {
        this.userDal = new UserDalImp();
    }

    public User save(String name, String login, String password, String passwordConfirmation, boolean isRoot) {
        log.info("[save] - Cadastrando o usuário: {}", login);

        if (login.length() < 5) {
            throw new LoginException("O login deve possuir no mínimo 5 caracters!");
        }

        if (!password.equals(passwordConfirmation)) {
            throw new PasswordException("Os campos \"senha\" e \"confirmar senha\" devem ser iguais!");
        }

        if (!PasswordUtils.validate(password)) {
            StringBuilder error = new StringBuilder("<html>A senha escolhida é muito fraca.");
            error.append("<br /><br />Tente criar uma senha com os requisitos abaixo.");
            error.append("<br />- minimo de 8 caracters.");
            error.append("<br />- Precisa ter no minimo um caractere especial.");
            error.append("<br />- Precisa ter no minimo uma letra maiúscula.");
            error.append("<br />- Precisa ter no minimo uma letra minúscula.</html>");

            throw new PasswordException(error.toString());
        }

        try {
            return this.userDal.save(name, login, PasswordUtils.encryptPassword(password), isRoot);
        } catch (NoSuchAlgorithmException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException("[Erro interno] - Não foi possível cadastrar o usuário!");
        }
    }

    public User save(Long id, String name, String login, boolean isRoot) {
        log.info("[save] - Editando o usuário: {}", id);

        if (login.length() < 5) {
            throw new LoginException("O login deve possuir no mínimo 5 caracters!");
        }

        return this.userDal.save(id, name, login, isRoot);
    }

    public User save(Long id, String name, String login, String password, String passwordConfirmation, boolean isRoot) {
        log.info("[save] - Editando o usuário: {}", id);

        if (login.length() < 5) {
            throw new LoginException("O login deve possuir no mínimo 5 caracters!");
        }

        if (!password.equals(passwordConfirmation)) {
            throw new PasswordException("Os campos \"senha\" e \"confirmar senha\" devem ser iguais!");
        }

        if (!PasswordUtils.validate(password)) {
            StringBuilder error = new StringBuilder("<html>A senha escolhida é muito fraca.");
            error.append("<br /><br />Tente criar uma senha com os requisitos abaixo.");
            error.append("<br />- minimo de 8 caracters.");
            error.append("<br />- Precisa ter no minimo um caractere especial.");
            error.append("<br />- Precisa ter no minimo uma letra maiúscula.");
            error.append("<br />- Precisa ter no minimo uma letra minúscula.</html>");

            throw new PasswordException(error.toString());
        }

        try {
            return this.userDal.save(id, name, login, PasswordUtils.encryptPassword(password), isRoot);
        } catch (NoSuchAlgorithmException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException("[Erro interno] - Não foi possível cadastrar o usuário!");
        }
    }

    public List<User> findAll() {
        log.info("[findAll] - obtendo todos os usuários");

        return this.userDal.findAll();
    }

    public List<User> findAll(int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "usuário" : "usuários") + " da página {}", quantity, page);

        return this.userDal.findAll(quantity, page);
    }

    public List<User> findBy(String feature) {
        log.info("[findBy] - procurando usuários. {}", feature);

        return this.userDal.findBy(feature);
    }

    public List<User> findBy(String feature, int quantity, int page) {
        log.info("[findBy] - procurando {} usuários que tenha algum atribuo que corresponda a '{}' na página {}", quantity, feature, page);

        return this.userDal.findBy(feature, quantity, page);
    }

    public void delete(User user) {
        log.info("[delete] - apagando usuário {}.", user.getId());

        this.userDal.delete(user);
    }

    public void deleteById(Long id) {
        log.info("[delete] - apagando usuário {}.", id);

        this.userDal.deleteById(id);
    }

    public int pageQuantity(int quantity) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (quantity == 1 ? "item" : "itens cada."), quantity);

        try {
            return this.userDal.pageQuantity(quantity);
        } catch (InternalException e) {
            return 0;
        }
    }

    public int pageQuantity(int quantity, String feature) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (quantity == 1 ? "item" : "itens cada."), quantity);

        try {
            return this.userDal.pageQuantity(quantity, feature);
        } catch (InternalException e) {
            return 0;
        }
    }
}
