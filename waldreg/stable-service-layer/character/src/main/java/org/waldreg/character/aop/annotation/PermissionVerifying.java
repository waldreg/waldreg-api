package org.waldreg.character.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionVerifying{

    String[] value() default "";

    VerifyingFailBehavior fail() default VerifyingFailBehavior.EXCEPTION;

}
