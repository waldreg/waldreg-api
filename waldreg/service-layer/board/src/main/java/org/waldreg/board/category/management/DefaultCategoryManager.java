package org.waldreg.board.category.management;

import java.util.List;
import org.waldreg.board.category.exception.CategoryDoesNotExistException;
import org.waldreg.board.category.exception.DuplicateCategoryNameException;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;

public class DefaultCategoryManager implements CategoryManager{

    private CategoryRepository categoryRepository;

    public DefaultCategoryManager(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createCategory(CategoryDto categoryDto){
        throwIfCategoryNameDuplicated(categoryDto.getCategoryName());
        categoryRepository.createCategory(categoryDto);
    }

    private void throwIfCategoryNameDuplicated(String categoryName){
        if(categoryRepository.isDuplicateCategoryName(categoryName)){
            throw new DuplicateCategoryNameException(categoryName);
        }
    }


    @Override
    public List<CategoryDto> inquiryAllCategory(){
        return categoryRepository.inquiryAllCategory();
    }

    @Override
    public void modifyCategory(CategoryDto categoryDto){
        throwIfCategoryDoesNotExist(categoryDto.getId());
        throwIfCategoryNameDuplicated(categoryDto.getCategoryName());
        categoryRepository.modifyCategory(categoryDto);
    }

    private void throwIfCategoryDoesNotExist(int categoryId){
        if (!categoryRepository.isExistCategory(categoryId)){
            throw new CategoryDoesNotExistException(categoryId);
        }
    }

}
