package org.waldreg.token.exception;

import io.jsonwebtoken.JwtException;

public class MisMatchException extends JwtException{
    public MisMatchException(){
        super("Mismatched token");
    }
}
