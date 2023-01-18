package org.waldreg.character.aop.parameter;

public final class PermissionVerifyState{

    private boolean isVerified;

    public PermissionVerifyState(){}

    public PermissionVerifyState(boolean status){
        isVerified = status;
    }
    public boolean isVerified(){
        return isVerified;
    }

}
