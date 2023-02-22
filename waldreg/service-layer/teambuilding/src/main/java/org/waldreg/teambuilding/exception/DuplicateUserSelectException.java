package org.waldreg.teambuilding.exception;

public class DuplicateUserSelectException extends RuntimeException{

    public DuplicateUserSelectException(String userId){super("Cannot add user \"" + userId + "\" in team cause already in other team");}

}
