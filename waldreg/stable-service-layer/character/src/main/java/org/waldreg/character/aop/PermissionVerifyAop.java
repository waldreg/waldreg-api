package org.waldreg.character.aop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.permission.verification.PermissionVerifier;
import org.waldreg.util.AnnotationExtractor;
import org.waldreg.util.DecryptedTokenContextHolder;

@Service
@Order
@Aspect
public class PermissionVerifyAop{

    private final PermissionVerifier permissionVerifier;
    private final AnnotationExtractor<PermissionVerifying> annotationExtractor;
    private final DecryptedTokenContextHolder decryptedTokenContextHolder;
    private final CharacterInUserReadable characterInUserReadable;

    @Around("@annotation(org.waldreg.character.aop.annotation.PermissionVerifying)")
    public Object verify(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        PermissionVerifying permissionVerifying = annotationExtractor.extractAnnotation(proceedingJoinPoint, PermissionVerifying.class);
        CharacterDto characterDto = getCharacterDto();
        if (!isUserAccessible(permissionVerifying.value(), characterDto)){
            permissionVerifying.fail().behave();
        }
        try{
            return proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        } finally{
            decryptedTokenContextHolder.resolve();
        }
    }

    private CharacterDto getCharacterDto(){
        int id = decryptedTokenContextHolder.get();
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

    @Autowired
    public PermissionVerifyAop(PermissionVerifier permissionVerifier,
            AnnotationExtractor<PermissionVerifying> annotationExtractor,
            DecryptedTokenContextHolder decryptedTokenContextHolder,
            CharacterInUserReadable characterInUserReadable){
        this.permissionVerifier = permissionVerifier;
        this.annotationExtractor = annotationExtractor;
        this.decryptedTokenContextHolder = decryptedTokenContextHolder;
        this.characterInUserReadable = characterInUserReadable;
    }

}
