package org.waldreg.token.exception;

public class PasswordMissMatchException extends RuntimeException{

    public PasswordMissMatchException(){
        super("wrong Password");
    }
}
