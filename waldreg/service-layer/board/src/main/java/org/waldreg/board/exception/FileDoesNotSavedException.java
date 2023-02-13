package org.waldreg.board.exception;

public class FileDoesNotSavedException extends RuntimeException{
    private final String code;

    public FileDoesNotSavedException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
