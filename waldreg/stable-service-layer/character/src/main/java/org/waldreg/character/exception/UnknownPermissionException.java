package org.waldreg.character.exception;

public class UnknownPermissionException extends RuntimeException{

    public UnknownPermissionException(String name){
        super("Unknown permission name \"" + name + "\"");
    }

}
