package org.waldreg.board.board.file;

import java.util.List;
import java.util.concurrent.Future;

public interface FileInfoGettable{

    List<Future<String>> getSavedFileNameList();

    List<Future<String>> getSavedImageNameList();

    Future<Boolean> isFileDeleteSuccess();

}
