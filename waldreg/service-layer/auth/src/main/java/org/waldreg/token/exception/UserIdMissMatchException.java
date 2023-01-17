package org.waldreg.token.exception;

public class UserIdMissMatchException extends RuntimeException{

    public UserIdMissMatchException(String userId){
        super("Miss matched userId \"" + userId + "\"");
    }

}
