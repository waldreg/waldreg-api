package org.waldreg.character.exception;

public class NoPermissionException extends RuntimeException{

    public NoPermissionException(){
        super("No Permission");
    }

    public NoPermissionException(String name){
        super("No permission \"" + name + "\"");
    }

}
