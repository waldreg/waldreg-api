package org.waldreg.user.exception;

public class UnknownIdException extends RuntimeException{

    public UnknownIdException(int id){super("Unknown Id \"" + id + "\"");}

}
