package org.waldreg.token.aop.behavior;

import org.waldreg.token.exception.AuthenticateFailException;

public enum AuthenticateFailBehavior{

    THROW(()-> {throw new AuthenticateFailException();}),
    PASS(()->{});

    private final Failable failable;

    AuthenticateFailBehavior(Failable failable){
        this.failable = failable;
    }

    public void fail(){
        failable.fail();
    }

    @FunctionalInterface
    public interface Failable{
        void fail();
    }

}
