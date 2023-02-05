package org.waldreg.board.board.file;

import java.util.UUID;

public interface FileInfoGettable{

    String getSavedFileName(UUID id);

    String getDeletedFileName(UUID id);

}
