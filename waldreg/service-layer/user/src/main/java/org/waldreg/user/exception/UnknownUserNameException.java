package org.waldreg.user.exception;

public class UnknownUserNameException extends RuntimeException{

    public UnknownUserNameException(String name){super("Unknown user name \"" + name + "\"");}

}
