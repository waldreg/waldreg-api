package org.waldreg.repository.category;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.category.Category;
import org.waldreg.repository.MemoryCategoryStorage;

@Repository
public class MemoryCategoryRepository implements CategoryRepository{

    private final MemoryCategoryStorage memoryCategoryStorage;

    private final CategoryMapper categoryMapper;

    @Autowired
    public MemoryCategoryRepository(MemoryCategoryStorage memoryCategoryStorage, CategoryMapper categoryMapper){
        this.memoryCategoryStorage = memoryCategoryStorage;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Boolean isExistCategory(int categoryId){
        return null;
    }

    @Override
    public void createCategory(CategoryDto categoryDto){
        Category category = categoryMapper.CategoryDtoToCategoryDomain(categoryDto);
        memoryCategoryStorage.createCategory(category);
    }

    @Override
    public List<CategoryDto> inquiryAllCategory(){
        List<Category> categoryList = memoryCategoryStorage.inquiryAllCategory();
        return categoryMapper.categoryDomainListToCategoryDtoList(categoryList);
    }

    @Override
    public CategoryDto inquiryCategoryById(int id){
        return null;
    }

    @Override
    public void modifyCategory(CategoryDto categoryDto){

    }

    @Override
    public void deleteCategory(int id){

    }

    @Override
    public boolean isDuplicateCategoryName(String categoryName){
        return false;
    }

}
