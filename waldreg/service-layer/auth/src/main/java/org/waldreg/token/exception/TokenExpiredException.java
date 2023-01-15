package org.waldreg.token.exception;

public class TokenExpiredException extends RuntimeException{

    public TokenExpiredException(String message, Throwable cause){
        super("Expired token \"" + message + "\"" + cause + "\"");
    }

}
