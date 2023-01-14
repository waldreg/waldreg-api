package org.waldreg.character.exception;

public class UnknownPermissionStatusException extends RuntimeException{

    public UnknownPermissionStatusException(String permissionName, String status){
        super("Unknown permission \"" + permissionName + "\" status \"" + status + "\"");
    }

}
