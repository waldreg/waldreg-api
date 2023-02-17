package org.waldreg.teambuilding.exception;

public class ContentOverflowException extends RuntimeException{

    private final String code;

    public ContentOverflowException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){return code;}

}
