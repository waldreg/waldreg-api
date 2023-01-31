package org.waldreg.board.file.spi;

import java.io.File;

public interface FileRepository{

    File loadFile(String fileName);


    String getFileById(int id);

}
