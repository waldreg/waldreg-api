package org.waldreg.board.exception;

public class CommentDoesNotExistException extends RuntimeException{

    public CommentDoesNotExistException(int commentId){
        super("Unknown comment id : " + commentId);
    }

}
