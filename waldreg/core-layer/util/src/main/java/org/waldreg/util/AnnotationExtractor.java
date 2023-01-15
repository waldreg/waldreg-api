package org.waldreg.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Service;

@Service
public class AnnotationExtractor<T extends Annotation>{

    public T extractAnnotation(JoinPoint joinPoint, Class<T> annotationType){
        String methodName = joinPoint.getSignature().getName();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        T ans = null;
        Method[] methods = targetClass.getMethods();
        for(Method method : methods){
            if(method.getName().equals(methodName)){
                ans = method.getAnnotation(annotationType);
                break;
            }
        }
        return ans;
    }

}
