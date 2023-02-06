package org.waldreg.board.exception;

public class ContentLengthOverThousandException extends RuntimeException{

    public ContentLengthOverThousandException(){
        super("Overflow content");
    }

}
