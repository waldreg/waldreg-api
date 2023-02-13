package org.waldreg.board.exception;

public class DuplicateCategoryNameException extends RuntimeException{
    private final String code;

    public DuplicateCategoryNameException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
