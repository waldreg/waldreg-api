package org.waldreg.attendance.exception;

public final class AlreadyAttendanceException extends RuntimeException{

    public AlreadyAttendanceException(){
        super("Already attendance");
    }

}
