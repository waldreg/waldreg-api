package org.wadlreg.reward.tag.exception;

public class UnknownRewardTagException extends RuntimeException{

    public UnknownRewardTagException(int id){
        super("Unknown reward tag id \"" + id + "\"");
    }

}
