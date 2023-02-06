package org.waldreg.board.exception;

public class DeletePermissionException extends RuntimeException{

    public DeletePermissionException(){
        super("Delete permission does not have");
    }

}
