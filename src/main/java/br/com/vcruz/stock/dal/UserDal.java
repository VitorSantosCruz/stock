package br.com.vcruz.stock.dal;

import br.com.vcruz.stock.model.User;

/**
 *
 * @author vcruz
 */
public interface UserDal {

    User findByLogin(String login);
}
