package org.waldreg.board.board.spi;

import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryPerformer{

    void addBoardInCategoryBoardList(BoardDto boardDto);

    boolean isExistCategory(int categoryId);
    CategoryDto inquiryCategoryById(int id);

}
