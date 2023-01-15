package org.waldreg.token.spi;

import org.waldreg.token.dto.TokenUserDto;

public interface AuthRepository {

    TokenUserDto findUserByUserIdPw(String userId, String userPassword);

    TokenUserDto findUserById(int id);

}
