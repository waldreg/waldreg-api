package org.waldreg.token.authenticator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.jwt.authenticator.JwtAuthenticator;
import org.waldreg.token.jwt.publisher.JwtTokenPublisher;
import org.waldreg.token.jwt.secret.Secret;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JwtTokenPublisher.class, JwtAuthenticator.class, Secret.class})
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


}
