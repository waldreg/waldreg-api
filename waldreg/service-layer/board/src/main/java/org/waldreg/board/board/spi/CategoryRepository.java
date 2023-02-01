package org.waldreg.board.board.spi;

import java.util.List;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryRepository{

    List<CategoryDto> inquiryAllCategory();

    CategoryDto inquiryCategory(int id);

    Boolean isExistCategory(int categoryId);

}
