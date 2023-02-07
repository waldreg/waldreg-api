package org.waldreg.board.exception;

public class BoardModifyPermissionException extends RuntimeException{

    public BoardModifyPermissionException(){
        super("Modify permission does not have");
    }
}
