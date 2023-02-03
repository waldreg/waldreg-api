package org.waldreg.board.board.exception;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException(int from, int to){
        super("Invalid range\nfrom : " + from + " to : " + to);
    }

}
