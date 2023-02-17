package org.waldreg.teambuilding.exception;

public class UnknownUserIdException extends RuntimeException{

    public UnknownUserIdException(String userId){super("Unknown user_id \"" + userId + "\"");}

}
