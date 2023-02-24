package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.User;
import java.util.List;

/**
 *
 * @author vcruz
 */
public interface UserDal {

    User save(String name, String login, String password, boolean isRoot);

    User update(Long id, String name, String login, boolean isRoot);

    User update(Long id, String name, String login, String password, boolean isRoot);

    List<User> findAll(int quantity, int page);

    List<User> findBy(String feature, int quantity, int page);

    User findById(Long id);
    
    User findByLogin(String login);

    void deleteById(Long id);

    int pageQuantity(int quantity, String feature);
}
