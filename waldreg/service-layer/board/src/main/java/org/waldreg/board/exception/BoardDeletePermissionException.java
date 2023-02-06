package org.waldreg.board.exception;

public class BoardDeletePermissionException extends RuntimeException{

    public BoardDeletePermissionException(){
        super("Delete permission does not have");
    }

}
