package org.waldreg.character.exception;

public class UnknownPermissionStatusException extends RuntimeException{

    public UnknownPermissionStatusException(int permissionId, String permissionName, String status){
        super("Unknown permission id \"" + permissionId + "\" name \"" + permissionName + "\" status \"" + status + "\"");
    }

}
