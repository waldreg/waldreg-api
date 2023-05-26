package org.waldreg.board.file.spi;

public interface BoardFileNameRepository{

    void saveFileName(String origin, String uuid);

    String getUUIDByOrigin(String origin);

    String getOriginByUUID(String uuid);

    void deleteFileNameByUUID(String uuid);

}
