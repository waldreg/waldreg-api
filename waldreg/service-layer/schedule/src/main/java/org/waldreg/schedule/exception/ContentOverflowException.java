package org.waldreg.schedule.exception;

public class ContentOverflowException extends RuntimeException{

    public ContentOverflowException(String message){super("Overflow content \"" + message + "\" (Valid value <= 1000)");}

}
