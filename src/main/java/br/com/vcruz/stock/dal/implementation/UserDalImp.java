package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.exception.InternalException;
import br.com.vcruz.stock.exception.NotFoundException;
import br.com.vcruz.stock.model.User;
import br.com.vcruz.stock.utils.DateConverterUtils;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class UserDalImp implements UserDal {

    @Override
    public User findByLogin(String login) {
        try {
            String query = "select * from user where binary login = ? and is_deleted = false ";

            Connection connection = ConnectionConfig.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, login);

            User user = this.getResult(preparedStatement);

            return user;
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findByLogin] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    private User getResult(PreparedStatement preparedStatement) throws NotFoundException, SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("created_Date"));
                LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("last_modified_date"));
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                boolean isRoot = resultSet.getBoolean("is_root");
                int loginAttempCont = resultSet.getInt("login_attemp_cont");
                LocalDateTime localDateTimeBlockedUntil = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("blocked_until"));
                LocalTime blockedUntil = localDateTimeBlockedUntil == null ? null : localDateTimeBlockedUntil.toLocalTime();
                boolean isDeleted = resultSet.getBoolean("is_deleted");

                return User.builder()
                        .createdDate(createdDate)
                        .lastModifiedDate(lastModifiedDate)
                        .id(id)
                        .name(name)
                        .login(login)
                        .password(password)
                        .isRoot(isRoot)
                        .loginAttempCont(loginAttempCont)
                        .blockedUntil(blockedUntil)
                        .isDeleted(isDeleted)
                        .build();
            } else {
                String errorMessage = "Esse usuário não existe!";
                throw new NotFoundException(errorMessage);
            }
        } catch (NotFoundException | SQLException e) {
            throw e;
        }
    }
}
