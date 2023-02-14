package org.waldreg.attendance.exception;

public class UnknownAttendanceTypeException extends RuntimeException{

    public UnknownAttendanceTypeException(String typeName){
        super("Unknown attendance type \"" + typeName + "\"");
    }

}
