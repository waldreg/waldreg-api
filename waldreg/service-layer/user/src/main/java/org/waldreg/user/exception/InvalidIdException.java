package org.waldreg.user.exception;

public class InvalidIdException extends RuntimeException{

    public InvalidIdException(int id){super("Invalid Id \"" + id + "\"");}

}
