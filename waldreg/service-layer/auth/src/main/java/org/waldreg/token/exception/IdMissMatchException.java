package org.waldreg.token.exception;

public class IdMissMatchException extends RuntimeException{

    public IdMissMatchException(int authorizedId, int targetId){
        super("Miss matched authorizedId \"" + authorizedId + "\" but targetId was \"" + targetId + "\"");
    }

}
