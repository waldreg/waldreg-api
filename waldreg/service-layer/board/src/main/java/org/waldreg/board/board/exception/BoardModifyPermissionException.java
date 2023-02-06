package org.waldreg.board.board.exception;

public class BoardModifyPermissionException extends RuntimeException{

    public BoardModifyPermissionException(){
        super("Modify permission does not have");
    }
}
