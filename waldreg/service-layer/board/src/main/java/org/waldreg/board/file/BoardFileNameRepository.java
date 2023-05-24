package org.waldreg.board.file;

public interface BoardFileNameRepository{

    void saveFileName(String origin, String uuid);

    String getUUIDByOrigin(String origin);

}
