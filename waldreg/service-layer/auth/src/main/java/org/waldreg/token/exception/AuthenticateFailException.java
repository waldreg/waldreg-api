package org.waldreg.token.exception;

public class AuthenticateFailException extends RuntimeException{

    public AuthenticateFailException(){
        super("Authenticate fail");
    }

}
