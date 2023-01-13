package org.waldreg.token.publisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.TokenPublisher;
import org.waldreg.token.dto.TokenDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {publisherImpl.class})
public class publisherTest{

    @Autowired
    private TokenPublisher jwtTokenPublisher;

    @Autowired
    private TokenPublisher superTokenPulisher;

    @Test
    @DisplayName("jwt 토큰 생성 성공")
    public void CREATE_JWT_TOKEN_SUCCESS_TEST() throws Exception{

        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder()
                .id(id)
                .build();

        //when

        //then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(jwtTokenPublisher.publish(tokenDto))
        );

    }


    @Test
    @DisplayName("super 토큰 생성 성공")
    public void CREATE_SUPER_TOKEN_SUCCESS_TEST(){
        //given
        int id = 1;
        TokenDto tokenDto = TokenDto.builder().id(id).build();

        //when

        //then
        Assertions.assertAll(
                ()-> Assertions.assertDoesNotThrow(superTokenPulisher.publish(TokenDto))
        );
    }


}
