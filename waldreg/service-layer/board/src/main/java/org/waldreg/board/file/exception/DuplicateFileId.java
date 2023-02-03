package org.waldreg.board.file.exception;

public final class DuplicateFileId extends RuntimeException{

    public DuplicateFileId(String id){
        super("Duplicated file id \"" + id + "\"");
    }

}
