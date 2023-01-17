package org.waldreg.token.aop;

import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.annotation.UserIdAuthenticating;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.exception.UserIdMissMatchException;
import org.waldreg.util.annotation.AnnotationExtractor;

@Aspect
@Service
public class AuthenticateAop{

    private final TokenUserFindById tokenUserFindById;
    private final TokenAuthenticator tokenAuthenticator;
    private final HttpServletRequest httpServletRequest;
    private final AnnotationExtractor<Authenticating> authenticatingAnnotationExtractor;
    private final AnnotationExtractor<HeaderPasswordAuthenticating> headerPasswordAuthenticatingAnnotationExtractor;
    private final AnnotationExtractor<UserIdAuthenticating> userIdAuthenticatingAnnotationExtractor;

    @Autowired
    public AuthenticateAop(TokenUserFindById tokenUserFindById,
            TokenAuthenticator tokenAuthenticator,
            HttpServletRequest httpServletRequest,
            AnnotationExtractor<Authenticating> authenticatingAnnotationExtractor,
            AnnotationExtractor<HeaderPasswordAuthenticating> headerPasswordAuthenticatingAnnotationExtractor,
            AnnotationExtractor<UserIdAuthenticating> userIdAuthenticatingAnnotationExtractor) {
        this.tokenUserFindById = tokenUserFindById;
        this.tokenAuthenticator = tokenAuthenticator;
        this.httpServletRequest = httpServletRequest;
        this.authenticatingAnnotationExtractor = authenticatingAnnotationExtractor;
        this.headerPasswordAuthenticatingAnnotationExtractor = headerPasswordAuthenticatingAnnotationExtractor;
        this.userIdAuthenticatingAnnotationExtractor = userIdAuthenticatingAnnotationExtractor;
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.Authenticating)")
    public Object authenticate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Authenticating authenticating = authenticatingAnnotationExtractor.extractAnnotation(proceedingJoinPoint, Authenticating.class);
        try{
            int id = getDecryptedId(getToken());
            tokenUserFindById.findUserById(id);
        }catch(Exception E){
            authenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating)")
    public Object authenticateByHeaderPassword(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        HeaderPasswordAuthenticating headerPasswordAuthenticating = headerPasswordAuthenticatingAnnotationExtractor
                .extractAnnotation(proceedingJoinPoint, HeaderPasswordAuthenticating.class);
        try{
            int id = getDecryptedId(getToken());
            TokenUserDto tokenUserDto = tokenUserFindById.findUserById(id);
            throwIfUserPasswordDoesNotSame(tokenUserDto, getRequestPassword());
        }catch(Exception E){
            headerPasswordAuthenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    private String getRequestPassword(){
        return httpServletRequest.getHeader("Password");
    }

    private void throwIfUserPasswordDoesNotSame(TokenUserDto tokenUserDto, String requestPassword){
        if(!tokenUserDto.getUserPassword().equals(requestPassword)){
            throw new PasswordMissMatchException(tokenUserDto.getUserId(), requestPassword);
        }
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.UserIdAuthenticating)")
    public Object authenticateByUserId(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        UserIdAuthenticating userIdAuthenticating = userIdAuthenticatingAnnotationExtractor
                .extractAnnotation(proceedingJoinPoint, UserIdAuthenticating.class);
        try{
            int id = getDecryptedId(getToken());
            TokenUserDto tokenUserDto = tokenUserFindById.findUserById(id);
            throwIfUserIdDoesNotSame(tokenUserDto, proceedingJoinPoint, userIdAuthenticating.idx());
        }catch(Exception E){
            userIdAuthenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
    }

    private String getToken(){
        return httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private int getDecryptedId(String token) throws TokenExpiredException{
        return tokenAuthenticator.authenticate(token);
    }

    private void throwIfUserIdDoesNotSame(TokenUserDto tokenUserDto, ProceedingJoinPoint proceedingJoinPoint, int argumentIdx){
        String userId = getParameterArgument(proceedingJoinPoint, argumentIdx, String.class);
        if(!tokenUserDto.getUserId().equals(userId)){
            throw new UserIdMissMatchException(userId);
        }
    }

    private <T> T getParameterArgument(ProceedingJoinPoint proceedingJoinPoint, int idx, Class<T> type){
        Object[] parameterArguments = proceedingJoinPoint.getArgs();
        throwIfParameterLengthLessThen(parameterArguments, idx);
        throwIfCannotCastArgumentTypeTo(parameterArguments[idx], type);
        return type.cast(parameterArguments[idx]);
    }

    private void throwIfParameterLengthLessThen(Object[] parameterArguments, int idx){
        if(parameterArguments.length <= idx) {
            throw new IllegalStateException("Can not find parameter idx at \"" + idx + "\"");
        }
    }

    private <T> void throwIfCannotCastArgumentTypeTo(Object parameterArgument, Class<T> type){
        try{
            type.cast(parameterArgument);
        }catch(ClassCastException CCE){
            throw new IllegalArgumentException("Can not cast parameter type to " + type.getSimpleName());
        }
    }

}
