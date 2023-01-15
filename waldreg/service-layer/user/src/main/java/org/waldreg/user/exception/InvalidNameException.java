package org.waldreg.user.exception;

public class InvalidNameException extends InvalidInputException{

    public InvalidNameException(String name){super("Invalid input : name \"" + name + "\"");}

}
