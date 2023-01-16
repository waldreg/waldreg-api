package org.waldreg.token.exception;

import io.jsonwebtoken.JwtException;

public class AuthenticateFailException extends RuntimeException{

    public AuthenticateFailException(){
        super("Authenticate fail");
    }

}
