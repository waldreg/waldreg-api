package org.waldreg.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.auth.response.AuthTokenResponse;
import org.waldreg.auth.response.TemporaryTokenResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.publisher.TokenPublisher;
import org.waldreg.token.spi.TokenUserFindUserIdAndPassword;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class AuthController{

    private final TokenPublisher tokenPublisher;
    private final TokenUserFindUserIdAndPassword tokenUserFindUserIdAndPassword;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;


    @Autowired
    public AuthController(TokenPublisher tokenPublisher,
                          TokenUserFindUserIdAndPassword tokenUserFindUserIdAndPassword, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.tokenPublisher = tokenPublisher;
        this.tokenUserFindUserIdAndPassword = tokenUserFindUserIdAndPassword;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @PostMapping("/token")
    public AuthTokenResponse getToken(@RequestBody @Validated AuthTokenRequest authRequest){
        TokenUserDto tokenUserDto = tokenUserFindUserIdAndPassword.findUserByUserIdPassword(authRequest.getUserId(), authRequest.getUserPassword());
        TokenDto tokenDto = TokenDto.builder().id(tokenUserDto.getId()).build();
        String accessToken = tokenPublisher.publish(tokenDto);
        String tokenType = "Bearer";
        return AuthTokenResponse.builder().accessToken(accessToken).tokenType(tokenType).build();
    }

    @Authenticating
    @GetMapping("/token")
    public TemporaryTokenResponse getTemporaryToken(){
        int id = getDecryptedId();
        TokenDto tokenDto = TokenDto.builder()
                .id(id).expiredMilliSec(30000).build();
        String temporaryToken = tokenPublisher.publish(tokenDto);
        String tokenType = "Bearer";
        return TemporaryTokenResponse.builder().temporaryToken(temporaryToken).tokenType(tokenType).build();

    }

    private int getDecryptedId(){
        return decryptedTokenContextGetter.get();
    }

}
