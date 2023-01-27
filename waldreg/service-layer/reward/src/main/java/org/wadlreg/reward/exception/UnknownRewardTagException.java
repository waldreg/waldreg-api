package org.wadlreg.reward.exception;

public class UnknownRewardTagException extends RuntimeException{

    public UnknownRewardTagException(int id){
        super("Unknown reward tag id \"" + id + "\"");
    }

}
