package org.waldreg.token.aop.annotation;

import org.waldreg.token.aop.behavior.AuthFailBehavior;

public @interface IdAuthenticating{

    int idx() default 0;

    AuthFailBehavior fail() default  AuthFailBehavior.THROW;

}
