package org.waldreg.token.aop;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.AuthenticateFailException;
import org.waldreg.token.spi.AuthRepository;
import org.waldreg.util.annotation.AnnotationExtractor;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthenticateAop.class, AnnotationExtractor.class})
public class AuthenticateAopTest{

    @Autowired
    private AuthenticateAop authenticateAop;
    @MockBean
    private AuthRepository authRepository;
    @MockBean
    private TokenAuthenticator tokenAuthenticator;
    @MockBean
    private HttpServletRequest httpServletRequest;
    @MockBean
    private AuthenticateAopClient authenticateAopClient;
    private final int id = 1;
    private final String userId = "admin";
    private final String userPassword = "0000";
    private final TokenUserDto tokenUserDto = TokenUserDto.builder()
            .id(id)
            .userId(userId)
            .userPassword(userPassword)
            .build();

    @BeforeEach
    @AfterEach
    public void SET_UP_AUTHENTICATE_AOP_TEST(){
        Mockito.when(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("mock_token");
        Mockito.when(httpServletRequest.getHeader("Password")).thenReturn(userPassword);
        Mockito.when(tokenAuthenticator.authenticate(Mockito.anyString())).thenReturn(id);
        Mockito.when(authRepository.findUserById(id)).thenReturn(tokenUserDto);
    }

    @Test
    @DisplayName("인증 성공 테스트")
    public void AUTHENTICATE_SUCCESS_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(authenticateAopClient);
        aspectJProxyFactory.addAspect(authenticateAop);
        AuthenticateAopClient authenticateAopClient = aspectJProxyFactory.getProxy();

        // when & then
        Assertions.assertDoesNotThrow(authenticateAopClient::authenticate);
    }

    @Test
    @DisplayName("userId 로 인증 성공 테스트")
    public void AUTHENTICATE_BY_USER_ID_SUCCESS_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(authenticateAopClient);
        aspectJProxyFactory.addAspect(authenticateAop);
        AuthenticateAopClient authenticateAopClient = aspectJProxyFactory.getProxy();

        // when & then
        Assertions.assertDoesNotThrow(() -> authenticateAopClient.authenticateByUserId(1, "admin"));
    }

    @Test
    @DisplayName("userId 로 인증 실패 테스트 - 다른 userId")
    public void AUTHENTICATE_BY_USER_ID_FAIL_WRONG_USER_ID_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(authenticateAopClient);
        aspectJProxyFactory.addAspect(authenticateAop);
        AuthenticateAopClient authenticateAopClient = aspectJProxyFactory.getProxy();

        // when & then
        Assertions.assertThrows(AuthenticateFailException.class, () -> authenticateAopClient.authenticateByUserId(1, "guest"));
    }

    @Test
    @DisplayName("Header password 로 인증 성공 테스트")
    public void AUTHENTICATE_BY_HEADER_PASSWORD_SUCCESS_TEST(){
        // given
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(authenticateAopClient);
        aspectJProxyFactory.addAspect(authenticateAop);
        AuthenticateAopClient authenticateAopClient = aspectJProxyFactory.getProxy();

        // when & then
        Assertions.assertDoesNotThrow(authenticateAopClient::authenticateByHeaderPassword);
    }

}
