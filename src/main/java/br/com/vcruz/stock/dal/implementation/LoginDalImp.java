package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.LoginDal;
import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.exception.BlockedUserException;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.LoginException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class LoginDalImp implements LoginDal {

    private final UserDal userDal;
    private static final int MAX_LOGIN_ATTEMP = 3;

    public LoginDalImp() {
        this.userDal = new UserDalImp();
    }

    @Override
    public User login(String login, String password) {

        try {
            User user = this.userDal.findByLogin(login);

            if (user.isBlocked()) {
                throw new BlockedUserException("Usuário bloqueado, aguarde um pouco para tentar novamente!");
            }

            if (user.getLoginAttempCont() == LoginDalImp.MAX_LOGIN_ATTEMP) {
                this.resetCountLoginError(user);
            }

            if (!user.getPassword().equals(password)) {
                this.countLoginError(user);

                throw new LoginException("Usuário e/ou senha inválidos!");
            }

            if (user.getLoginAttempCont() > 0) {
                this.resetCountLoginError(user);
            }

            return user;
        } catch (NotFoundException | LoginException | BlockedUserException | InternalException e) {
            log.error("[login] - {}", e.getMessage());

            if (e instanceof NotFoundException || e instanceof LoginException) {
                throw new LoginException(e.getMessage());
            }

            if (e instanceof BlockedUserException) {
                throw new BlockedUserException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    private void countLoginError(User user) {
        String query = "update user set login_attemp_cont = login_attemp_cont + 1 ";

        if (user.getLoginAttempCont() + 1 == LoginDalImp.MAX_LOGIN_ATTEMP) {
            query += ", blocked_until = ? ";
        }

        query += "where login = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            if (user.getLoginAttempCont() + 1 == LoginDalImp.MAX_LOGIN_ATTEMP) {
                preparedStatement.setTime(1, new Time(DateConverterUtils.convertToDate(LocalDateTime.now().plusMinutes(5)).getTime()));
                preparedStatement.setString(2, user.getLogin());

                preparedStatement.executeUpdate();

                throw new BlockedUserException("Usuário bloqueado, aguarde um pouco para tentar novamente!");
            } else {
                preparedStatement.setString(1, user.getLogin());
                preparedStatement.executeUpdate();
            }
        } catch (BlockedUserException | IOException | SQLException e) {
            log.error("[countLoginError] - {}", e.getMessage());

            if (e instanceof BlockedUserException) {
                throw new BlockedUserException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    private void resetCountLoginError(User user) {
        String query = "update user set login_attemp_cont = 0, blocked_until = null where login = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getLogin());
            preparedStatement.executeUpdate();
        } catch (BlockedUserException | IOException | SQLException e) {
            log.error("[countLoginError] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }
}
