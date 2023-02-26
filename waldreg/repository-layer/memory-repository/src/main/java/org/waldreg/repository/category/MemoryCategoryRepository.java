package org.waldreg.repository.category;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;

@Repository
public class MemoryCategoryRepository implements CategoryRepository{

    private final MemoryCategoryStorage memoryCategoryStorage;

    private final MemoryBoardStorage memoryBoardStorage;

    private final CategoryMapper categoryMapper;

    @Autowired
    public MemoryCategoryRepository(MemoryCategoryStorage memoryCategoryStorage, MemoryBoardStorage memoryBoardStorage, CategoryMapper categoryMapper){
        this.memoryCategoryStorage = memoryCategoryStorage;
        this.memoryBoardStorage = memoryBoardStorage;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public void addBoardInCategoryBoardList(BoardDto boardDto){
        Board board = categoryMapper.boardDtoToBoardDomain(boardDto);
        memoryCategoryStorage.addBoardInCategoryBoardList(board);
    }

    @Override
    public boolean isExistCategory(int categoryId){
        return memoryCategoryStorage.inquiryCategoryById(categoryId)!=null;
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
        Category category = memoryCategoryStorage.inquiryCategoryById(id);
        return categoryMapper.categoryDomainToCategoryDto(category);
    }

    @Override
    public void modifyCategory(CategoryDto categoryDto){
        Category category = memoryCategoryStorage.inquiryCategoryById(categoryDto.getId());
        category.setCategoryName(categoryDto.getCategoryName());
        memoryCategoryStorage.modifyCategory(category);
    }

    @Override
    public void deleteCategory(int id){
        Category category = memoryCategoryStorage.inquiryCategoryById(id);
        deleteBoardInCategoryBoardList(category.getBoardList());
        memoryCategoryStorage.deleteCategory(id);
    }

    private void deleteBoardInCategoryBoardList(List<Board> boardList){
        for(Board board : boardList){
            memoryBoardStorage.deleteBoardById(board.getId());
        }
    }

    @Override
    public boolean isDuplicateCategoryName(String categoryName){
        return memoryCategoryStorage.isDuplicatedCategoryName(categoryName);
    }

}