package org.waldreg.board.category.management;

import java.util.List;
import org.waldreg.board.dto.CategoryDto;

public interface CategoryManager{

    void createCategory(CategoryDto categoryDto);

    List<CategoryDto> inquiryAllCategory();

    CategoryDto inquiryCategoryById(int categoryId);

    void modifyCategory(CategoryDto categoryDto);

    void deleteCategory(int id);

}
