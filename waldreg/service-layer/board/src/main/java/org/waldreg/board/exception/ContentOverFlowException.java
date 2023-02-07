package org.waldreg.board.exception;

public class ContentOverFlowException extends RuntimeException{

    public ContentOverFlowException(){
        super("BOARD-405 Overflow content");
    }

}
