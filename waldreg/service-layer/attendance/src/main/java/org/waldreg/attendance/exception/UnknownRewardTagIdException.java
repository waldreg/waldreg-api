package org.waldreg.attendance.exception;

public final class UnknownRewardTagIdException extends RuntimeException{

    public UnknownRewardTagIdException(int id){
        super("Unknown reward tag id \"" + id + "\"");
    }

}
