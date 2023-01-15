package org.waldreg.token.spi;

import org.waldreg.domain.user.User;

public interface AuthRepository {

    User findUserByUserIdPw(String userId,String userPassword);

    User findUserById(int id);

}
