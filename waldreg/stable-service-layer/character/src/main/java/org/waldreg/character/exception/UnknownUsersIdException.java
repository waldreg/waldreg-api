package org.waldreg.character.exception;

public class UnknownUsersIdException extends RuntimeException{

    public UnknownUsersIdException(int id){
        super("Cannot find user id \"" + id + "\"");
    }

}
