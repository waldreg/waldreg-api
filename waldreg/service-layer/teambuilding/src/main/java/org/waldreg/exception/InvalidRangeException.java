package org.waldreg.exception;

public class InvalidRangeException extends RuntimeException{

    public InvalidRangeException(int startIdx, int endIdx){
        super("Invalid range start-idx \""+startIdx+"\", end-idx \""+endIdx+"\"");
    }
}
