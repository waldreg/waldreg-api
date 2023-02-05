package org.waldreg.board.comment.exception;

public class BoardDoesNotExistException extends RuntimeException{

    public BoardDoesNotExistException(int boardId){
        super("Board Does Not Exist\nBoardId: " + boardId);
    }

}
