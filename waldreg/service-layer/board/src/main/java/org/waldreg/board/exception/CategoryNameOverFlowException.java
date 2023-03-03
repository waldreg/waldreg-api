package org.waldreg.board.exception;

public class CategoryNameOverFlowException extends RuntimeException{

    private final String code;

    public CategoryNameOverFlowException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
