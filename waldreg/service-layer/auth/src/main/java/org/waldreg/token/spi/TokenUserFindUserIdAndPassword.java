package org.waldreg.token.spi;

import org.waldreg.token.dto.TokenUserDto;

public interface TokenUserFindUserIdAndPassword{

    TokenUserDto findUserByUserIdPassword(String userId, String userPassword);

}
