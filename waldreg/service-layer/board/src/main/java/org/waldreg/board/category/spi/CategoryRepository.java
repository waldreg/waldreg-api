package org.waldreg.board.category.spi;

import java.util.List;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryRepository{

    void createCategory(CategoryDto categoryDto);

    List<CategoryDto> inquiryAllCategory();

    CategoryDto inquiryCategoryById(int id);

    void modifyCategory(CategoryDto categoryDto);

    void deleteCategory(int id);

    boolean isDuplicateCategoryName(String categoryName);

    boolean isExistCategory(int categoryId);

}
