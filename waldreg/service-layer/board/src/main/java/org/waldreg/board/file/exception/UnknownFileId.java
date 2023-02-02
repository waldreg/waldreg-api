package org.waldreg.board.file.exception;

public final class UnknownFileId extends RuntimeException{

    public UnknownFileId(String id){
        super("Unknown file id \"" + id + "\"");
    }

}
