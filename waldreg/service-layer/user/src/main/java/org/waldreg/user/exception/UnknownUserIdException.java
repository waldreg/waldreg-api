package org.waldreg.user.exception;

public class UnknownUserIdException extends RuntimeException{

    public UnknownUserIdException(String userId){super("Unknown userId \"" + userId + "\"");}

}
