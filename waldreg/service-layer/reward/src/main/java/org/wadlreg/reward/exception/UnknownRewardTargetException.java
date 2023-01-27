package org.wadlreg.reward.exception;

public class UnknownRewardAssignTargetException extends RuntimeException{

    public UnknownRewardAssignTargetException(int id){
        super("Unknown user id \"" + id + "\"");
    }

}
