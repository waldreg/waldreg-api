package org.waldreg.board.comment.exception;

public class UserDoesNotExistException extends RuntimeException{

    public UserDoesNotExistException(int authorId){
        super("User Does Not Exist\nauthorId " + authorId);
    }

}
