package org.waldreg.board.exception;

public class CommentDoesNotExistException extends RuntimeException{
    private final String code;

    public CommentDoesNotExistException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
