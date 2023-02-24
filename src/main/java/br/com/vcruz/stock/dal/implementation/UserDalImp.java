package br.com.vcruz.stock.dal.implementation;

import br.com.vcruz.stock.configuration.ConnectionConfig;
import br.com.vcruz.stock.dal.UserDal;
import br.com.vcruz.stock.exception.DuplicateException;
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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;

/**
 *
 * @author vcruz
 */
@Log4j2
public class UserDalImp implements UserDal {

    @Override
    public User save(String name, String login, String password, boolean isRoot) {
        String query = "insert into user (name, login, password, is_root) values (?, ?, ?, ?)";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            preparedStatement.setBoolean(4, isRoot);

            preparedStatement.executeUpdate();

            return this.findByLogin(login);
        } catch (IOException | SQLException e) {
            log.error("[save] - {}", e.getMessage());

            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateException("O login escolhido não está disponível, tente outro.");
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public User update(Long id, String name, String login, boolean isRoot) {
        String query = "update user set last_modified_date = now(), name = ?, login = ?, is_root = ? where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.setBoolean(3, isRoot);
            preparedStatement.setLong(4, id);

            preparedStatement.executeUpdate();

            return this.findByLogin(login);
        } catch (IOException | SQLException e) {
            log.error("[update] - {}", e.getMessage());

            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateException("O login escolhido não está disponível, tente outro.");
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public User update(Long id, String name, String login, String password, boolean isRoot) {
        String query = "update user set last_modified_date = now(), name = ?, login = ?, password = ?, is_root = ? where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            preparedStatement.setBoolean(4, isRoot);
            preparedStatement.setLong(5, id);

            preparedStatement.executeUpdate();

            return this.findByLogin(login);
        } catch (IOException | SQLException e) {
            log.error("[update] - {}", e.getMessage());

            if (e.getMessage().contains("Duplicate")) {
                throw new DuplicateException("O login escolhido não está disponível, tente outro.");
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<User> findAll(int quantity, int page) {
        String query = "select * from user where is_deleted = false limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setInt(2, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findAll] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public List<User> findBy(String feature, int quantity, int page) {
        String query = "select * from user where (id = ? or name like ? or login like ?) and is_deleted = false  limit ? offset ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, "%" + feature + "%");
            preparedStatement.setString(2, "%" + feature + "%");
            preparedStatement.setString(3, "%" + feature + "%");
            preparedStatement.setInt(4, quantity);
            preparedStatement.setInt(5, (page * quantity));

            return this.getResult(preparedStatement);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findBy] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public User findById(Long id) {
        String query = "select * from user where id = ? and is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            List<User> userList = this.getResult(preparedStatement);

            if (userList.isEmpty()) {
                String errorMessage = "Esse usuário não existe!";
                throw new NotFoundException(errorMessage);
            }

            return userList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findById] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public User findByLogin(String login) {
        String query = "select * from user where binary login = ? and is_deleted = false";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);

            List<User> userList = this.getResult(preparedStatement);

            if (userList.isEmpty()) {
                String errorMessage = "Esse usuário não existe!";
                throw new NotFoundException(errorMessage);
            }

            return userList.get(0);
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[findByLogin] - {}", e.getMessage());

            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            }

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public void deleteById(Long id) {
        String query = "update user set last_modified_date = now(), is_deleted = true where id = ?";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (IOException | SQLException e) {
            log.error("[deleteById] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    @Override
    public int pageQuantity(int quantity, String feature) {
        String query = "select ceiling(count(*) / ?) as pageQuantity from user where (id = ? or name like ? or login like ?) and is_deleted = false;";

        try (Connection connection = ConnectionConfig.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, quantity);
            preparedStatement.setString(2, "%" + feature + "%");
            preparedStatement.setString(3, "%" + feature + "%");
            preparedStatement.setString(4, "%" + feature + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("pageQuantity");
            } catch (SQLException e) {
                throw e;
            }
        } catch (NotFoundException | IOException | SQLException e) {
            log.error("[pageQuantity] - {}", e.getMessage());

            throw new InternalException(e.getMessage());
        }
    }

    private List<User> getResult(PreparedStatement preparedStatement) throws NotFoundException, SQLException {

        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<User> users = new ArrayList<>();

            while (resultSet.next()) {
                users.add(UserDalImp.getUserFromResultSet(resultSet));
            }

            return users;
        } catch (SQLException e) {
            throw e;
        }
    }

    public static User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        LocalDateTime createdDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("user.created_Date"));
        LocalDateTime lastModifiedDate = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("user.last_modified_date"));
        Long id = resultSet.getLong("user.id");
        String name = resultSet.getString("user.name");
        String login = resultSet.getString("user.login");
        String password = resultSet.getString("user.password");
        boolean isRoot = resultSet.getBoolean("user.is_root");
        int loginAttempCont = resultSet.getInt("user.login_attemp_cont");
        LocalDateTime localDateTimeBlockedUntil = DateConverterUtils.convertToLocalDateTime(resultSet.getTimestamp("user.blocked_until"));
        LocalTime blockedUntil = localDateTimeBlockedUntil == null ? null : localDateTimeBlockedUntil.toLocalTime();
        boolean isDeleted = resultSet.getBoolean("user.is_deleted");

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
    }
}
