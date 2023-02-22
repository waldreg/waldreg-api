package org.waldreg.attendance.exception;

public final class DoesNotRegisteredAttendanceException extends RuntimeException{

    public DoesNotRegisteredAttendanceException(){
        super("Not registered attendance list");
    }

}
