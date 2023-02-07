package org.waldreg.board.exception;

public class FutureCannotGetException extends RuntimeException{

    public FutureCannotGetException(){
        super("Future cannot get result");
    }

}
