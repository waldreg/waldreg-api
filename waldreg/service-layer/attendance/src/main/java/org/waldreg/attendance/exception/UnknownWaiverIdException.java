package org.waldreg.attendance.exception;

public final class UnknownWaiverIdException extends RuntimeException{

    public UnknownWaiverIdException(int waiverId){
        super("Unknown waiver id \"" + waiverId + "\"");
    }

}
