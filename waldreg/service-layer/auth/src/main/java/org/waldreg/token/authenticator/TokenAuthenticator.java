package org.waldreg.token.authenticator;

import org.waldreg.token.dto.TokenUserDto;

public interface TokenAuthenticator{

    TokenUserDto getTokenUserDtoByUserIdAndPassword(String userId, String userPassword);

    int authenticate(String token);

}
