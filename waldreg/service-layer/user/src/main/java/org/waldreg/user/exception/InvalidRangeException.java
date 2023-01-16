package org.waldreg.user.exception;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException(int stIdx, int enIdx){super("Invalid Range \"(" + stIdx + " , " + enIdx + ")\"");}

}
