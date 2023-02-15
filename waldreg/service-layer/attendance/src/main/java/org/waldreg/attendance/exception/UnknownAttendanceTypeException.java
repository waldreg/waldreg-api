package org.waldreg.attendance.exception;

public final class UnknownAttendanceTypeException extends RuntimeException{

    public UnknownAttendanceTypeException(String typeName){
        super("Unknown attendance type \"" + typeName + "\"");
    }

}
