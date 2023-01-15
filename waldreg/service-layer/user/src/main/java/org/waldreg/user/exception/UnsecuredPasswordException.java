package org.waldreg.user.exception;

public class UnsecuredPasswordException extends RuntimeException{

    public UnsecuredPasswordException(String password){super("Unsecured password \"" + password + "\"");}

}
