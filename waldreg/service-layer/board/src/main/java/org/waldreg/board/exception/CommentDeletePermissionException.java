package org.waldreg.board.exception;

public class CommentDeletePermissionException extends RuntimeException{

    public CommentDeletePermissionException(){
        super("Delete permission does not have");
    }

}
