package org.waldreg.token.aop.behavior;

import org.waldreg.token.exception.AuthenticateFailException;

public enum AuthFailBehavior{

    THROW(()-> {throw new AuthenticateFailException();}),
    PASS(()->{});

    private final Failable failable;

    AuthFailBehavior(Failable failable){
        this.failable = failable;
    }

    public void behave(){
        failable.fail();
    }

    @FunctionalInterface
    public interface Failable{
        void fail();
    }

}
