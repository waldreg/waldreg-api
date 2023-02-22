package org.waldreg.attendance.exception;

public final class WrongAttendanceIdentifyException extends RuntimeException{

    public WrongAttendanceIdentifyException(String identify){
        super("Wrong attendance identify \"" + identify + "\"");
    }

}
