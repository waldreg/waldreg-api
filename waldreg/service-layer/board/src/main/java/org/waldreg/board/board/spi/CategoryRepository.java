package org.waldreg.board.board.spi;

import java.util.List;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryRepository{

    List<CategoryDto> inquiryAllCategory();

    Boolean isExistCategory(int categoryId);

}
