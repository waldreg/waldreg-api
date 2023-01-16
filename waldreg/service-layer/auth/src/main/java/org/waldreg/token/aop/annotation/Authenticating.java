package org.waldreg.token.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.waldreg.token.aop.behavior.AuthenticateFailBehavior;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticating{

    int idx() default 0;

    String headerName();

    AuthenticateFailBehavior fail() default AuthenticateFailBehavior.THROW;

}
