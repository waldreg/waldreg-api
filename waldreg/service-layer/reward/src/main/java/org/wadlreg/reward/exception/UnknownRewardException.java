package org.wadlreg.reward.exception;

public class UnknownRewardException extends RuntimeException{

    public UnknownRewardException(int id){
        super("Unknown reward id \"" + id + "\"");
    }

}
