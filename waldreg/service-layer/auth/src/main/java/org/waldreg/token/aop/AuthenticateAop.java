package org.waldreg.token.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.BoardIdAuthenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.annotation.IdAuthenticating;
import org.waldreg.token.aop.annotation.UserIdAuthenticating;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;
import org.waldreg.token.authenticator.TokenAuthenticator;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.IdMissMatchException;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.exception.UserIdMissMatchException;
import org.waldreg.util.annotation.AnnotationExtractor;

@Aspect
@Order(100000)
@Service
public class AuthenticateAop{

    private final TokenUserFindable tokenUserFindable;
    private final TokenAuthenticator tokenAuthenticator;
    private final HttpServletRequest httpServletRequest;
    private final AnnotationExtractor<Authenticating> authenticatingAnnotationExtractor;
    private final AnnotationExtractor<HeaderPasswordAuthenticating> headerPasswordAuthenticatingAnnotationExtractor;
    private final AnnotationExtractor<UserIdAuthenticating> userIdAuthenticatingAnnotationExtractor;
    private final AnnotationExtractor<IdAuthenticating> idAuthenticatingAnnotationExtractor;
    private final AnnotationExtractor<BoardIdAuthenticating> boardIdAuthenticatingAnnotationExtractor;

    @Autowired
    public AuthenticateAop(TokenUserFindable tokenUserFindable,
                           TokenAuthenticator tokenAuthenticator,
                           HttpServletRequest httpServletRequest,
                           AnnotationExtractor<Authenticating> authenticatingAnnotationExtractor,
                           AnnotationExtractor<HeaderPasswordAuthenticating> headerPasswordAuthenticatingAnnotationExtractor,
                           AnnotationExtractor<UserIdAuthenticating> userIdAuthenticatingAnnotationExtractor,
                           AnnotationExtractor<IdAuthenticating> idAuthenticatingAnnotationExtractor,
                           AnnotationExtractor<BoardIdAuthenticating> boardIdAuthenticatingAnnotationExtractor) {
        this.tokenUserFindable = tokenUserFindable;
        this.tokenAuthenticator = tokenAuthenticator;
        this.httpServletRequest = httpServletRequest;
        this.authenticatingAnnotationExtractor = authenticatingAnnotationExtractor;
        this.headerPasswordAuthenticatingAnnotationExtractor = headerPasswordAuthenticatingAnnotationExtractor;
        this.userIdAuthenticatingAnnotationExtractor = userIdAuthenticatingAnnotationExtractor;
        this.idAuthenticatingAnnotationExtractor = idAuthenticatingAnnotationExtractor;
        this.boardIdAuthenticatingAnnotationExtractor = boardIdAuthenticatingAnnotationExtractor;
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.Authenticating)")
    public Object authenticate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        Authenticating authenticating = authenticatingAnnotationExtractor.extractAnnotation(proceedingJoinPoint, Authenticating.class);
        boolean verifyState = true;
        try{
            int id = getDecryptedId(getToken());
            TokenUserDto tokenUserDto = tokenUserFindable.findUserById(id);
        }catch(Exception E){
            verifyState = false;
            authenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(setPermissionVerifyStateParameter(proceedingJoinPoint, verifyState));
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating)")
    public Object authenticateByHeaderPassword(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        HeaderPasswordAuthenticating headerPasswordAuthenticating = headerPasswordAuthenticatingAnnotationExtractor
                .extractAnnotation(proceedingJoinPoint, HeaderPasswordAuthenticating.class);
        boolean verifyState = true;
        try{
            int id = getDecryptedId(getToken());
            TokenUserDto tokenUserDto = tokenUserFindable.findUserById(id);
            throwIfUserPasswordDoesNotSame(tokenUserDto, getRequestPassword());
        }catch(Exception E){
            verifyState = false;
            headerPasswordAuthenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(setPermissionVerifyStateParameter(proceedingJoinPoint, verifyState));
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
        boolean verifyState = true;
        try{
            int id = getDecryptedId(getToken());
            TokenUserDto tokenUserDto = tokenUserFindable.findUserById(id);
            throwIfUserIdDoesNotSame(tokenUserDto, proceedingJoinPoint, userIdAuthenticating.idx());
        }catch(Exception E){
            verifyState = false;
            userIdAuthenticating.fail().behave();
        }
        return proceedingJoinPoint.proceed(setPermissionVerifyStateParameter(proceedingJoinPoint, verifyState));
    }

    private void throwIfUserIdDoesNotSame(TokenUserDto tokenUserDto, ProceedingJoinPoint proceedingJoinPoint, int argumentIdx){
        String userId = getParameterArgument(proceedingJoinPoint, argumentIdx, String.class);
        if(!tokenUserDto.getUserId().equals(userId)){
            throw new UserIdMissMatchException(userId);
        }
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.IdAuthenticating)")
    public Object authenticateById(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        IdAuthenticating idAuthenticating = idAuthenticatingAnnotationExtractor
                .extractAnnotation(proceedingJoinPoint, IdAuthenticating.class);
        boolean verifyState = true;
        try{
            TokenUserDto tokenUserDto = tokenUserFindable.findUserById(getDecryptedId(getToken()));
            throwIfIdDoesNotSame(tokenUserDto.getId(), (int)proceedingJoinPoint.getArgs()[idAuthenticating.idx()]);
        }catch(Exception E){
            idAuthenticating.fail().behave();
            verifyState = false;
        }
        return proceedingJoinPoint.proceed(setPermissionVerifyStateParameter(proceedingJoinPoint, verifyState));
    }

    @Around("@annotation(org.waldreg.token.aop.annotation.BoardIdAuthenticating)")
    public Object authenticateByBoardId(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        BoardIdAuthenticating boardIdAuthenticating = boardIdAuthenticatingAnnotationExtractor
                .extractAnnotation(proceedingJoinPoint, BoardIdAuthenticating.class);
        boolean verifyState = true;
        try{
            int id = getDecryptedId(getToken());
            int boardId = getParameterArgument(proceedingJoinPoint, boardIdAuthenticating.idx(), Integer.class);
            TokenUserDto tokenUserDto = tokenUserFindable.findUserByBoardId(boardId);
            throwIfIdDoesNotSame(id, tokenUserDto.getId());
        }catch(Exception E){
            boardIdAuthenticating.fail().behave();
            verifyState = false;
        }
        return proceedingJoinPoint.proceed(setPermissionVerifyStateParameter(proceedingJoinPoint, verifyState));
    }

    private String getToken(){
        return httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private int getDecryptedId(String token) throws TokenExpiredException{
        return tokenAuthenticator.authenticate(token);
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

    private void throwIfIdDoesNotSame(int authorizedId, int targetId){
        if(authorizedId != targetId){
            throw new IdMissMatchException(authorizedId, targetId);
        }
    }

    private Object[] setPermissionVerifyStateParameter(JoinPoint joinPoint, boolean state){
        Object[] objects = joinPoint.getArgs();
        Parameter[] parameters = extractParameters(joinPoint);
        for (int i = 0; i < parameters.length; i++){
            if (parameters[i].getType().equals(AuthenticateVerifyState.class)){
                objects[i] = (AuthenticateVerifyState) (new AuthenticateVerifyState(state));
            }
        }
        return objects;
    }

    private Parameter[] extractParameters(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method[] methods = targetClass.getMethods();
        for (Method method : methods){
            if (method.getName().equals(methodName)){
                return method.getParameters();
            }
        }
        throw new IllegalStateException("Can not find method named \"" + methodName + "\"");
    }


}
