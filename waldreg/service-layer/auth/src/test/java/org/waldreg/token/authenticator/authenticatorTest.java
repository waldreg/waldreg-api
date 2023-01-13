package org.waldreg.token.authenticator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.TokenAuthenticator;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.TokenPublisher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {authenticatorImpl.class})
public class authenticatorTest{

    @Autowired
    private TokenPublisher jwtTokenPublisher;

    @Autowired
    private TokenPublisher superTokenPulisher;

    @Autowired
    private TokenAuthenticator jwtTokenAuthenticator;

    @Autowired
    private TokenAuthenticator superTokenAuthenticator;

    @Test
    @DisplayName("jwt 토큰 인증 성공")
    public void AUTHENTICATE_JWT_TOKEN_SUCCESS_TEST(){
        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder().id(id).build();

        //when
        String encryptedToken = jwtTokenPublisher.publish(tokenDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(jwtTokenAuthenticator.authenticate(encryptedToken))
        );

    }

    @Test
    @DisplayName("super 토큰 인증 성공")
    public void AUTHENTICATE_SUPER_TOKEN_SUCCESS_TEST(){
        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder().id(id).build();

        //when
        String encryptedToken = superTokenPulisher.publish(tokenDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(superTokenAuthenticator.authenticate(encryptedToken))
        );
    }


}
