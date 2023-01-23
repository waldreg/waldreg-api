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
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.permission.verification.PermissionVerifier;
import org.waldreg.util.annotation.AnnotationExtractor;
import org.waldreg.util.exception.DecryptedTokenDoesNotExistException;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@Service
@Order
@Aspect
public class PermissionVerifyAop{

    private final PermissionVerifier permissionVerifier;
    private final AnnotationExtractor<PermissionVerifying> annotationExtractor;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;
    private final CharacterInUserReadable characterInUserReadable;

    @Autowired
    public PermissionVerifyAop(PermissionVerifier permissionVerifier,
            AnnotationExtractor<PermissionVerifying> annotationExtractor,
            DecryptedTokenContextGetter decryptedTokenContextGetter,
            CharacterInUserReadable characterInUserReadable){
        this.permissionVerifier = permissionVerifier;
        this.annotationExtractor = annotationExtractor;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
        this.characterInUserReadable = characterInUserReadable;
    }

    @Around("@annotation(org.waldreg.character.aop.annotation.PermissionVerifying)")
    public Object verify(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        PermissionVerifying permissionVerifying = annotationExtractor.extractAnnotation(proceedingJoinPoint, PermissionVerifying.class);
        if(!isDecryptedTokenExist()){
            return proceedFail(permissionVerifying, proceedingJoinPoint);
        }
        CharacterDto characterDto = getCharacterDto();
        if (!isUserAccessible(permissionVerifying.value(), characterDto)){
            return proceedFail(permissionVerifying, proceedingJoinPoint);
        }
        Object[] args = setPermissionVerifyStateParameter(proceedingJoinPoint, true);
        return proceedingJoinPoint.proceed(args);
    }

    private boolean isDecryptedTokenExist(){
        try{
            decryptedTokenContextGetter.get();
        } catch (DecryptedTokenDoesNotExistException E){
            return false;
        }
        return true;
    }

    private CharacterDto getCharacterDto(){
        int id = decryptedTokenContextGetter.get();
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
        if (!permissionDtoMap.containsKey(permissionName)){throw new UnknownPermissionException(permissionName);}
    }

    private Object proceedFail(PermissionVerifying permissionVerifying, ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        permissionVerifying.fail().behave();
        Object[] args = setPermissionVerifyStateParameter(proceedingJoinPoint, false);
        return proceedingJoinPoint.proceed(args);
    }

    private Object[] setPermissionVerifyStateParameter(JoinPoint joinPoint, boolean state){
        Object[] objects = joinPoint.getArgs();
        Parameter[] parameters = extractParameters(joinPoint);
        for (int i = 0; i < parameters.length; i++){
            if (parameters[i].getType().equals(PermissionVerifyState.class)){
                objects[i] = (PermissionVerifyState) (new PermissionVerifyState(state));
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
