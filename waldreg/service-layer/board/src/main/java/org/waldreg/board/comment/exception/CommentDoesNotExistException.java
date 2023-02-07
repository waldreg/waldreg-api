package org.waldreg.board.comment.exception;

public class CommentDoesNotExistException extends RuntimeException{

    public CommentDoesNotExistException(int commentId){
        super("Comment Does Not Exist\nBoardId: " + commentId);
    }

}