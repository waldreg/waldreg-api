package org.waldreg.board.comment.exception;

public class ContentLengthOverThousandException extends RuntimeException{

    public ContentLengthOverThousandException(){
        super("Overflow content");
    }

}
