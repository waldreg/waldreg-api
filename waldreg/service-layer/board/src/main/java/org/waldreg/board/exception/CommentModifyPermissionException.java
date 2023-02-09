package org.waldreg.board.exception;

public class CommentModifyPermissionException extends RuntimeException{

    public CommentModifyPermissionException(){
        super("Modify permission does not have");
    }
}
