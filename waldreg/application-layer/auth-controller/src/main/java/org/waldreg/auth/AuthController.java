package org.waldreg.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.auth.response.AuthTokenResponse;
import org.waldreg.auth.response.TemporaryTokenResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenDto;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.publisher.TokenPublisher;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class AuthController{

    private final TokenPublisher tokenPublisher;
    private final TokenAuthenticator tokenAuthenticator;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;


    @Autowired
    public AuthController(TokenPublisher tokenPublisher,
                          TokenAuthenticator tokenAuthenticator, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.tokenPublisher = tokenPublisher;
        this.tokenAuthenticator = tokenAuthenticator;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @XssFiltering
    @PostMapping("/token")
    public AuthTokenResponse getToken(@Xss("json") @RequestBody @Validated AuthTokenRequest authRequest){
        TokenUserDto tokenUserDto = tokenAuthenticator.getTokenUserDtoByUserIdAndPassword(authRequest.getUserId(), authRequest.getUserPassword());
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
