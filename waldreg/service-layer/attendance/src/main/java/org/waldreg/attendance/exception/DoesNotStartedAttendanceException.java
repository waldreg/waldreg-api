package org.waldreg.attendance.exception;

public final class DoesNotStartedAttendanceException extends RuntimeException{

    public DoesNotStartedAttendanceException(){
        super("Does not started attendance");
    }

}
