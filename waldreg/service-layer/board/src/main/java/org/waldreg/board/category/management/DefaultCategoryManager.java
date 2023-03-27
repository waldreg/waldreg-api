package org.waldreg.board.category.management;

import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.board.exception.CategoryDoesNotExistException;
import org.waldreg.board.exception.CategoryNameOverFlowException;
import org.waldreg.board.exception.DuplicateCategoryNameException;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;

@Service
public class DefaultCategoryManager implements CategoryManager{

    private CategoryRepository categoryRepository;

    public DefaultCategoryManager(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createCategory(CategoryDto categoryDto){
        throwIfCategoryNameOverFlow(categoryDto.getCategoryName());
        throwIfCategoryNameDuplicated(categoryDto.getCategoryName());
        categoryRepository.createCategory(categoryDto);
    }

    @Override
    public List<CategoryDto> inquiryAllCategory(){
        return categoryRepository.inquiryAllCategory();
    }

    @Override
    public CategoryDto inquiryCategoryById(int categoryId){
        throwIfCategoryDoesNotExist(categoryId);
        return categoryRepository.inquiryCategoryById(categoryId);
    }

    @Override
    public void modifyCategory(CategoryDto categoryDto){
        throwIfCategoryDoesNotExist(categoryDto.getId());
        throwIfCategoryNameOverFlow(categoryDto.getCategoryName());
        throwIfCategoryNameDuplicated(categoryDto.getCategoryName());
        categoryRepository.modifyCategory(categoryDto);
    }

    private void throwIfCategoryNameOverFlow(String categoryName){
        if (categoryName.length() > 50){
            throw new CategoryNameOverFlowException("BOARD-416", "Overflow category name : " + categoryName);
        }
    }

    private void throwIfCategoryNameDuplicated(String categoryName){
        if (categoryRepository.isDuplicateCategoryName(categoryName)){
            throw new DuplicateCategoryNameException("BOARD-412", "Duplicated category name : " + categoryName);
        }
    }

    @Override
    public void deleteCategory(int id){
        throwIfCategoryDoesNotExist(id);
        categoryRepository.deleteCategory(id);
    }

    private void throwIfCategoryDoesNotExist(int categoryId){
        if (!categoryRepository.isExistCategory(categoryId)){
            throw new CategoryDoesNotExistException("BOARD-403", "Unknown category id : " + categoryId);
        }
    }


}
