package org.waldreg.board.exception;

public class ReactionTypeDoesNotExistException extends RuntimeException{
    private final String code;

    public ReactionTypeDoesNotExistException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
