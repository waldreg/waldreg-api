package org.waldreg.token.aop;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.behavior.AuthenticateFailBehavior;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.spi.AuthRepository;
import org.waldreg.util.AnnotationExtractor;

@Service
@Order(5000)
@Aspect
public class TokenAuthenticateAop{

    private final TokenAuthenticator tokenAuthenticator;
    private final AuthRepository authRepository;
    private final HttpServletRequest httpServletRequest;
    private final AnnotationExtractor<Authenticating> annotationExtractor;

    @Autowired
    public TokenAuthenticateAop(TokenAuthenticator tokenAuthenticator,
                                AuthRepository authRepository,
                                HttpServletRequest httpServletRequest,
                                AnnotationExtractor<Authenticating> annotationExtractor){
        this.tokenAuthenticator = tokenAuthenticator;
        this.authRepository = authRepository;
        this.httpServletRequest = httpServletRequest;
        this.annotationExtractor = annotationExtractor;
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.Authenticating)")
    public Object authenticateToken(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Authenticating authenticating = annotationExtractor
                .extractAnnotation(proceedingJoinPoint, Authenticating.class);
        AuthenticateFailBehavior authenticateFailBehavior = authenticating.fail();
        String token = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        int id = tokenAuthenticator.authenticate(token);
        TokenUserDto tokenUserDto = authRepository.findUserById(id);
        if (isSamePasswordHeaderName(authenticating, "password")){
            if (!isSameUserPassword(tokenUserDto, httpServletRequest.getHeader("password"))){
                authenticateFailBehavior.fail();
            }
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        }
        int idx = authenticating.idx();
        Object[] args = proceedingJoinPoint.getArgs();
        if (args[idx] instanceof Integer){
            if (!isSameId(tokenUserDto, (int) args[idx])){
                authenticateFailBehavior.fail();
            }
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        }
        if (args[idx] instanceof String){
            if (!isSameName(tokenUserDto, (String) args[idx])){
                authenticateFailBehavior.fail();
            }
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        }
        throw new IllegalStateException("Cannot auth user cause there is no parameter authenticate able");
    }

    private boolean isSamePasswordHeaderName(Authenticating authenticating, String name){
        return authenticating.headerName() != null && authenticating.headerName().equals(name);
    }

    private boolean isSameUserPassword(TokenUserDto tokenUserDto, String password){
        return password.equals(tokenUserDto.getUserPassword());
    }

    private boolean isSameId(TokenUserDto tokenUserDto, int id){
        return tokenUserDto.getId() == id;
    }

    private boolean isSameName(TokenUserDto tokenUserDto, String name){
        return tokenUserDto.getName().equals(name);
    }

}
