package org.waldreg.token.spi;

import org.waldreg.token.dto.TokenUserDto;

public interface AuthRepository{

    TokenUserDto findUserByUserIdPassword(String userId, String userPassword);

    TokenUserDto findUserById(int id);


}
