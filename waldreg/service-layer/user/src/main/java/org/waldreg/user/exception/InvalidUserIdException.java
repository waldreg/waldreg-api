package org.waldreg.user.exception;

public class InvalidUserIdException extends InvalidInputException{

    public InvalidUserIdException(String userId){super("Invalid input : user_id \"" + userId + "\"");}

}
