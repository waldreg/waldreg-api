package org.waldreg.schedule.exception;

public class InvalidRepeatException extends RuntimeException{

    public InvalidRepeatException(String message){super("Invalid repeat due to \"" + message + "\"");}

}
