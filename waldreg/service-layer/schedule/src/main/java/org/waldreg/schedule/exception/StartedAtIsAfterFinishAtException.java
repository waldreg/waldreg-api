package org.waldreg.schedule.exception;

public class StartedAtIsAfterFinishAtException extends RuntimeException{

    public StartedAtIsAfterFinishAtException(){super("The end date of a Schedule cannot precede the start date.");}

}
