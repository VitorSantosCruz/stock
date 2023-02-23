package br.com.vcruz.stock.service;

import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.dal.implementation.UserDalImp;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.ValidationException;
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

        this.userValidation(name, login, password, passwordConfirmation);

        try {
            return this.userDal.save(name, login, PasswordUtils.encryptPassword(password), isRoot);
        } catch (NoSuchAlgorithmException e) {
            log.error("[save] - {}", e.getMessage());

            throw new InternalException("[Erro interno] - Não foi possível cadastrar o usuário!");
        }
    }

    public User update(Long id, String name, String login, boolean isRoot) {
        log.info("[update] - Editando o usuário: {}", id);

        this.userValidation(name, login);

        return this.userDal.update(id, name, login, isRoot);
    }

    public User update(Long id, String name, String login, String password, String passwordConfirmation, boolean isRoot) {
        log.info("[update] - Editando o usuário e alterando a senha: {}", id);

        this.userValidation(name, login, password, passwordConfirmation);

        try {
            return this.userDal.update(id, name, login, PasswordUtils.encryptPassword(password), isRoot);
        } catch (NoSuchAlgorithmException e) {
            log.error("[update] - {}", e.getMessage());

            throw new InternalException("[Erro interno] - Não foi possível cadastrar o usuário!");
        }
    }

    public List<User> findAll(int quantity, int page) {
        log.info("[findAll] - obtendo {} " + (quantity == 1 ? "usuário" : "usuários") + " da página {}", quantity, page);

        return this.userDal.findAll(quantity, page);
    }

    public List<User> findBy(String feature, int quantity, int page) {
        log.info("[findBy] - procurando {} usuários que tenha algum atribuo que corresponda a '{}' na página {}", quantity, feature, page);

        return this.userDal.findBy(feature, quantity, page);
    }

    public void deleteById(Long id) {
        log.info("[delete] - apagando usuário {}.", id);

        this.userDal.deleteById(id);
    }

    public int pageQuantity(int quantity) {
        return this.pageQuantity(quantity, "");
    }

    public int pageQuantity(int quantity, String feature) {
        log.info("[pageQuantity] - obtendo quantidade de págias com {} " + (quantity == 1 ? "item" : "itens cada."), quantity);

        try {
            return this.userDal.pageQuantity(quantity, feature);
        } catch (InternalException e) {
            return 0;
        }
    }

    private void nameValidation(String name) {
        if (name.isBlank()) {
            throw new ValidationException("O nome não pode ser vazio!");
        }
    }

    private void loginValidation(String login) {
        if (login.length() < 5) {
            throw new ValidationException("O login deve possuir no mínimo 5 caracters!");
        }
    }

    private void passwordValidation(String password, String passwordConfirmation) {
        if (!password.equals(passwordConfirmation)) {
            throw new ValidationException("Os campos \"senha\" e \"confirmar senha\" devem ser iguais!");
        }

        if (!PasswordUtils.validate(password)) {
            StringBuilder error = new StringBuilder("<html>A senha escolhida é muito fraca.");
            error.append("<br /><br />Tente criar uma senha com os requisitos abaixo.");
            error.append("<br />- minimo de 8 caracters.");
            error.append("<br />- Precisa ter no minimo um caractere especial.");
            error.append("<br />- Precisa ter no minimo uma letra maiúscula.");
            error.append("<br />- Precisa ter no minimo uma letra minúscula.</html>");

            throw new ValidationException(error.toString());
        }
    }

    private void userValidation(String name, String login) {
        this.nameValidation(name);
        this.loginValidation(login);
    }

    private void userValidation(String name, String login, String password, String passwordConfirmation) {
        this.nameValidation(name);
        this.loginValidation(login);
        this.passwordValidation(password, passwordConfirmation);
    }
}
