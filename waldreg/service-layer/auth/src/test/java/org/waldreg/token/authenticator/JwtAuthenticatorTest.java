package org.waldreg.token.authenticator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.jwt.authenticator.JwtAuthenticator;
import org.waldreg.token.jwt.publisher.JwtTokenPublisher;
import org.waldreg.token.jwt.secret.Secret;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JwtTokenPublisher.class, JwtAuthenticator.class, Secret.class, DecryptedTokenContext.class})
public class JwtAuthenticatorTest{

    @Autowired
    private JwtTokenPublisher jwtTokenPublisher;

    @Autowired
    private JwtAuthenticator jwtTokenAuthenticator;

    @Test
    @DisplayName("jwt 토큰 인증 성공")
    public void AUTHENTICATE_JWT_TOKEN_SUCCESS_TEST(){
        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder().id(id).build();

        //when
        String encryptedToken = jwtTokenPublisher.publish(tokenDto);

        //then
        Assertions.assertTrue(jwtTokenAuthenticator.authenticate(encryptedToken));

    }

    @Test
    @DisplayName("만료된 jwt 토큰")
    public void EXPIRED_JWT_TOKEN_TEST(){
        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder()
                .id(id)
                .expiredMilliSec(0)
                .build();
        //when
        String expiredToken = jwtTokenPublisher.publish(tokenDto);

        //then
        Assertions.assertThrows(TokenExpiredException.class, ()->jwtTokenAuthenticator.authenticate(expiredToken));
    }

}
