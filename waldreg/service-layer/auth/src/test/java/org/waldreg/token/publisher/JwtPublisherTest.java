package org.waldreg.token.publisher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.jwt.publisher.JwtTokenPublisher;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JwtTokenPublisher.class})
public class JwtPublisherTest{

    @Autowired
    private TokenPublisher jwtTokenPublisher;

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
        Assertions.assertDoesNotThrow(jwtTokenPublisher.publish(tokenDto));

    }


}
