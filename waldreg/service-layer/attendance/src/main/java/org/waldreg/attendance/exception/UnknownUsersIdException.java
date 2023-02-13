package org.waldreg.attendance.exception;

public final class UnknownUsersIdException extends RuntimeException{

    public UnknownUsersIdException(int id){
        super("Unknown user id \"" + id + "\"");
    }

}
