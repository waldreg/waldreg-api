package org.waldreg.user.exception;

public class InvalidPhoneNumberException extends InvalidInputException{

    public InvalidPhoneNumberException(String phoneNumber){super("Invalid input : user_id \"" + phoneNumber + "\"");}

}
