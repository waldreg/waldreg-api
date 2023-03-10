package org.waldreg.board.exception;

public class BoardDoesNotExistException extends RuntimeException{
    private final String code;

    public BoardDoesNotExistException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
