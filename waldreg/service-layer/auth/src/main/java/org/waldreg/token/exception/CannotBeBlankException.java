package org.waldreg.token.exception;

public class CannotBeBlankException extends RuntimeException{

    public CannotBeBlankException(){
        super("Cannot be blank user_id or user_password");
    }

}
