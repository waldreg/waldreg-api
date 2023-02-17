package org.waldreg.schedule.exception;

public final class InvalidDateFormatException extends RuntimeException{

    private final String code;

    public InvalidDateFormatException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
