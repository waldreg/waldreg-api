package org.waldreg.character.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.parameter.PermissionVerifyState;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.permission.verification.PermissionVerifier;
import org.waldreg.util.annotation.AnnotationExtractor;
import org.waldreg.util.token.DecryptedTokenContext;

@Service
@Order
@Aspect
public class PermissionVerifyAop{

    private final PermissionVerifier permissionVerifier;
    private final AnnotationExtractor<PermissionVerifying> annotationExtractor;
    private final DecryptedTokenContext decryptedTokenContext;
    private final CharacterInUserReadable characterInUserReadable;

    @Autowired
    public PermissionVerifyAop(PermissionVerifier permissionVerifier,
            AnnotationExtractor<PermissionVerifying> annotationExtractor,
            DecryptedTokenContext decryptedTokenContext,
            CharacterInUserReadable characterInUserReadable){
        this.permissionVerifier = permissionVerifier;
        this.annotationExtractor = annotationExtractor;
        this.decryptedTokenContext = decryptedTokenContext;
        this.characterInUserReadable = characterInUserReadable;
    }

    @Around("@annotation(org.waldreg.character.aop.annotation.PermissionVerifying)")
    public Object verify(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        PermissionVerifying permissionVerifying = annotationExtractor.extractAnnotation(proceedingJoinPoint, PermissionVerifying.class);
        CharacterDto characterDto = getCharacterDto();
        if (!isUserAccessible(permissionVerifying.value(), characterDto)){
            permissionVerifying.fail().behave();
            Object[] args = setPermissionVerifyStateParameter(proceedingJoinPoint, false);
            return proceedingJoinPoint.proceed(args);
        }
        Object[] args = setPermissionVerifyStateParameter(proceedingJoinPoint, true);
        return proceedingJoinPoint.proceed(args);
    }

    private CharacterDto getCharacterDto(){
        int id = decryptedTokenContext.get();
        return characterInUserReadable.readCharacterByUserId(id);
    }

    private boolean isUserAccessible(String[] permissionNameArray, CharacterDto characterDto){
        Map<String, String> permissionDtoMap = permissionDtoListToMap(characterDto.getPermissionList());
        for (String permissionName : permissionNameArray){
            throwIfUnknownPermissionNameDetected(permissionName, permissionDtoMap);
            if (!permissionVerifier.verify(permissionName, permissionDtoMap.get(permissionName))){
                return false;
            }
        }
        return true;
    }

    private Map<String, String> permissionDtoListToMap(List<PermissionDto> permissionDtoList){
        Map<String, String> permissionDtoMap = new HashMap<>();
        for (PermissionDto permissionDto : permissionDtoList){
            permissionDtoMap.put(permissionDto.getName(), permissionDto.getStatus());
        }
        return permissionDtoMap;
    }

    private void throwIfUnknownPermissionNameDetected(String permissionName, Map<String, String> permissionDtoMap){
        if (!permissionDtoMap.containsKey(permissionName)){throw new NoPermissionException(permissionName);}
    }

    private Object[] setPermissionVerifyStateParameter(JoinPoint joinPoint, boolean state){
        Object[] objects = joinPoint.getArgs();
        Parameter[] parameters = extractParameters(joinPoint);
        for (int i = 0; i < parameters.length; i++){
            if (parameters[i].getType().equals(PermissionVerifyState.class)){
                objects[i] = (PermissionVerifyState) (() -> state);
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