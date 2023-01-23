package org.waldreg.character.exception;

public class DuplicatedPermissionNameException extends RuntimeException{

    public DuplicatedPermissionNameException(String permissionName){
        super("Duplicated permission name \"" + permissionName + "\"");
    }

}
