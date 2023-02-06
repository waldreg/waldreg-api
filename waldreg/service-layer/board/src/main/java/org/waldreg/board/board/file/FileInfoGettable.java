package org.waldreg.board.board.file;

import java.util.concurrent.Future;

public interface FileInfoGettable{

    Future<String> getSavedFileName();

    Future<Boolean> isFileDeleteSuccess();

}
