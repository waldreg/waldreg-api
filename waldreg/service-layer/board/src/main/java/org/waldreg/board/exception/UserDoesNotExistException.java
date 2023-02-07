package org.waldreg.board.exception;

public class UserDoesNotExistException extends RuntimeException{
    private final String code;

    public UserDoesNotExistException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
