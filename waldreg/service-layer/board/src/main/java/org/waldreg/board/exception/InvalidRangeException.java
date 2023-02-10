package org.waldreg.board.exception;

public class InvalidRangeException extends RuntimeException{
    private final String code;

    public InvalidRangeException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
