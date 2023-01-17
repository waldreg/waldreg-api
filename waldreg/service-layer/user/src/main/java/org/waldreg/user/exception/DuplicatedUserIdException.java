package org.waldreg.user.exception;

public class DuplicatedUserIdException extends RuntimeException{

    public DuplicatedUserIdException(String userId){super("Duplicated user_id \"" + userId + "\"");}

}
