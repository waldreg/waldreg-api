package org.waldreg.teambuilding.exception;

public class InvalidTeamCountException extends RuntimeException{

    private final String code;

    public InvalidTeamCountException(String code, String message){
        super(message);
        this.code = code;
    }

    public String getCode(){
        return code;
    }

}
