package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.User;
import java.util.List;

/**
 *
 * @author vcruz
 */
public interface UserDal {

    User save(String name, String login, String password, boolean isRoot);

    User save(Long id, String name, String login, boolean isRoot);

    User save(Long id, String name, String login, String password, boolean isRoot);

    List<User> findAll();

    List<User> findAll(int quantity, int page);

    List<User> findBy(String feature);

    List<User> findBy(String feature, int quantity, int page);

    User findByLogin(String login);

    void delete(User user);

    void deleteById(Long id);

    int pageQuantity(int quantity);

    int pageQuantity(int quantity, String feature);
}
