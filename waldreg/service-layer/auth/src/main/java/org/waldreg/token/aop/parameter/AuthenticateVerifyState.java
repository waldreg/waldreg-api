package org.waldreg.token.aop.parameter;

public final class AuthenticateVerifyState{

    private boolean isVerified;

    public AuthenticateVerifyState(){}

    public AuthenticateVerifyState(boolean status){
        isVerified = status;
    }

    public boolean isVerified(){
        return isVerified;
    }

}
