package org.waldreg.board.board.spi;

public interface ReactionRepository{

    Boolean isUserExist(int boardId, int userId);

    void deleteFile(int imageId);

}
