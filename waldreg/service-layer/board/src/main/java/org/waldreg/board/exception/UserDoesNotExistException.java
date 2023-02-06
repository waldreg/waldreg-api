package org.waldreg.board.exception;

public class UserDoesNotExistException extends RuntimeException{

    public UserDoesNotExistException(int authorId){
        super("User Does Not Exist\nauthorId " + authorId);
    }

}
