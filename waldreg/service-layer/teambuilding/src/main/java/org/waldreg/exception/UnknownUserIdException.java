package org.waldreg.exception;

public class UnknownUserIdException extends RuntimeException{

    public UnknownUserIdException(String userId){super("Unknown user_id \"" + userId + "\"");}

}
