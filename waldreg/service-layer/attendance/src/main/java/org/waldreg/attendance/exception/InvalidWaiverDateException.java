package org.waldreg.attendance.exception;

public final class InvalidWaiverDateException extends RuntimeException{

    public InvalidWaiverDateException(){
        super("Invalid waiver date");
    }

}
