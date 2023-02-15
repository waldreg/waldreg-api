package org.waldreg.token.exception;

public class UnknownUserIdException extends RuntimeException{

    public UnknownUserIdException(String userId){
        super("Unknown user_id \"" + userId + "\"");
    }

}
