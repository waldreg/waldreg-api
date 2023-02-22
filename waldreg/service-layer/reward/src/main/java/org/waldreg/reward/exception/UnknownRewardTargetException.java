package org.waldreg.reward.exception;

public class UnknownRewardTargetException extends RuntimeException{

    public UnknownRewardTargetException(int id){
        super("Unknown user id \"" + id + "\"");
    }

}
