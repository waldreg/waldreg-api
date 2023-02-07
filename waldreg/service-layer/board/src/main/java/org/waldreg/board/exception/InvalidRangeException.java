package org.waldreg.board.exception;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException(int from, int to){
        super("Invalid range from : " + from + " to : " + to);
    }

}
