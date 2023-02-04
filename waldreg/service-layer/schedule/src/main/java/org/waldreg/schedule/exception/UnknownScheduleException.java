package org.waldreg.schedule.exception;

public final class UnknownScheduleException extends RuntimeException{

    private final String code;

    public UnknownScheduleException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
