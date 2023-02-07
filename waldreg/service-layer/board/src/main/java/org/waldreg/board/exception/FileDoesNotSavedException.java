package org.waldreg.board.exception;

public class FileDoesNotSavedException extends RuntimeException{

    public FileDoesNotSavedException(){
        super("File does not saved");
    }

}
