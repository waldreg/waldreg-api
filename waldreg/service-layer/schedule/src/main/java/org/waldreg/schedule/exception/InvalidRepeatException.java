package org.waldreg.schedule.exception;

public final class InvalidRepeatException extends RuntimeException{

    private final String code;

    public InvalidRepeatException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
