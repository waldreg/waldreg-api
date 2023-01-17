package org.waldreg.character.aop.behavior;

import org.waldreg.character.exception.NoPermissionException;

public enum VerifyingFailBehavior{

    EXCEPTION(() -> {throw new NoPermissionException();}),
    PASS(() -> {});

    private final Failable failable;

    VerifyingFailBehavior(Failable failable){
        this.failable = failable;
    }

    public void behave(){
        failable.fail();
    }

    @FunctionalInterface
    private interface Failable{

        void fail();

    }

}
