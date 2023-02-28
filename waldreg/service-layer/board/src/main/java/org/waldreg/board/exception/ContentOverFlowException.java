package org.waldreg.board.exception;

public class ContentOverFlowException extends RuntimeException{

    private final String code;

    public ContentOverFlowException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
