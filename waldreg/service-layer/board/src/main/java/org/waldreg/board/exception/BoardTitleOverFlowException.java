package org.waldreg.board.exception;

public class BoardTitleOverFlowException extends RuntimeException{

    private final String code;

    public BoardTitleOverFlowException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
