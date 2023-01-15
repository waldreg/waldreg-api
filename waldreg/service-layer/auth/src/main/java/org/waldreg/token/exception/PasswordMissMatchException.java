package org.waldreg.token.exception;

public class PasswordMisMatchException extends RuntimeException{

    public PasswordMisMatchException(){
        super("wrong Password");
    }
}
