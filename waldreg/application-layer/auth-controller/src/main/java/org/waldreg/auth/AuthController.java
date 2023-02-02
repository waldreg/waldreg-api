package org.waldreg.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.auth.response.AuthTokenResponse;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.publisher.TokenPublisher;
import org.waldreg.token.spi.AuthRepository;
import org.waldreg.token.spi.TokenUserFindUserIdAndPassword;

@RestController
public class AuthController{

    private final TokenPublisher tokenPublisher;

    private final TokenUserFindUserIdAndPassword tokenUserFindUserIdAndPassword;

    @Autowired
    public AuthController(TokenPublisher tokenPublisher,
            TokenUserFindUserIdAndPassword tokenUserFindUserIdAndPassword){
        this.tokenPublisher = tokenPublisher;
        this.tokenUserFindUserIdAndPassword = tokenUserFindUserIdAndPassword;
    }

    @PostMapping("/token")
    public AuthTokenResponse getToken(@RequestBody @Validated AuthTokenRequest authRequest){
        TokenUserDto tokenUserDto = tokenUserFindUserIdAndPassword.findUserByUserIdPassword(authRequest.getUserId(), authRequest.getUserPassword());
        TokenDto tokenDto = TokenDto.builder().id(tokenUserDto.getId()).build();
        String accessToken = tokenPublisher.publish(tokenDto);
        String tokenType = "Bearer";
        return AuthTokenResponse.builder().accessToken(accessToken).tokenType(tokenType).build();
    }

}
