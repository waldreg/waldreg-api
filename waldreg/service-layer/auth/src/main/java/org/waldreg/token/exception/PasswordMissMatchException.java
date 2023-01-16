package org.waldreg.token.exception;

public class PasswordMissMatchException extends RuntimeException{

    public PasswordMissMatchException(String userId, String wrongPassword){
        super("Wrong password \"" + "userId : " + userId + "\"wrongPassword :" + wrongPassword);
    }

}
