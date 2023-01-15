package org.waldreg.character.aop.annotation;

import org.waldreg.character.aop.behavior.VerifyingFailBehavior;

public @interface PermissionVerifying{

    String[] value() default "";

    VerifyingFailBehavior fail() default VerifyingFailBehavior.EXCEPTION;

}
