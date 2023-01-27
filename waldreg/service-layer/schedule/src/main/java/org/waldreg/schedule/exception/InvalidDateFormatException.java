package org.waldreg.schedule.exception;

public class InvalidDateFormatException extends RuntimeException{

    public InvalidDateFormatException(String message){super("Invalid date format due to \""+message+"\"");}

}
