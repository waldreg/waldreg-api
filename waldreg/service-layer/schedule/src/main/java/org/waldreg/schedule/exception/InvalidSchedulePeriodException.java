package org.waldreg.schedule.exception;

public final class InvalidSchedulePeriodException extends RuntimeException{

    private final String code;

    public InvalidSchedulePeriodException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
