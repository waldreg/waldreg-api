package org.waldreg.board.comment.exception;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException(int startIdx, int endIdx){
        super("Invalid range\nstartIdx : " + startIdx + " endIdx : " + endIdx);
    }

}
