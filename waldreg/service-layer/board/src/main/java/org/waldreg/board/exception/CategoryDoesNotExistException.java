package org.waldreg.board.exception;

public class CategoryDoesNotExistException extends RuntimeException{
    private final String code;

    public CategoryDoesNotExistException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
