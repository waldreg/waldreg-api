package org.waldreg.board.category.spi;

import java.util.List;
import org.waldreg.board.board.spi.BoardInCategoryRepository;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryRepository extends BoardInCategoryRepository{

    void createCategory(CategoryDto categoryDto);

    List<CategoryDto> inquiryAllCategory();
    void modifyCategory(CategoryDto categoryDto);

    void deleteCategory(int id);

    boolean isDuplicateCategoryName(String categoryName);


}
