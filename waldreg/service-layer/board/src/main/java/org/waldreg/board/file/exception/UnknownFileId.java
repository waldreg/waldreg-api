package org.waldreg.board.file.exception;

import java.io.FileNotFoundException;

public final class UnknownFileId extends RuntimeException{

    public UnknownFileId(String id){
        super("Unknown file id \"" + id + "\"");
    }

}
