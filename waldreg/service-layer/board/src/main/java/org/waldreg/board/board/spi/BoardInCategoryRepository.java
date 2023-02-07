package org.waldreg.board.board.spi;

import org.waldreg.board.dto.BoardDto;

public interface BoardInCategoryRepository{

    void addBoardInCategoryBoardList(BoardDto boardDto);

    boolean isExistCategory(int categoryId);

}
