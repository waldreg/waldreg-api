package org.waldreg.board.board.exception;

public class BlankTitleException extends RuntimeException{

    public BlankTitleException(){
        super("Title cannot be Blank");
    }

}
