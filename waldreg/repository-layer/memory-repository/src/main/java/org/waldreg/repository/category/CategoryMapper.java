package org.waldreg.repository.category;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;

@Service
public class CategoryMapper{

    private final BoardInCategoryMapper boardInCategoryMapper;

    @Autowired
    public CategoryMapper(BoardInCategoryMapper boardInCategoryMapper){
        this.boardInCategoryMapper = boardInCategoryMapper;
    }

    public Category CategoryDtoToCategoryDomain(CategoryDto categoryDto){
        Category.Builder builder = Category.builder()
                .categoryName(categoryDto.getCategoryName());
        if (!isCreateCategory(categoryDto)){
            return categoryDtoToCategoryDomainIfNotCreateCategory(categoryDto, builder);

        }
        return builder.build();
    }

    private boolean isCreateCategory(CategoryDto categoryDto){
        return categoryDto.getId() == 0;
    }

    private Category categoryDtoToCategoryDomainIfNotCreateCategory(CategoryDto categoryDto, Category.Builder builder){
        builder = builder.id(categoryDto.getId());
        if (isBoardDtoListNotEmpty(categoryDto.getBoardDtoList())){
            return builder.boardList(boardInCategoryMapper.boardDtoListToBoardDomainList(categoryDto.getBoardDtoList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isBoardDtoListNotEmpty(List<BoardDto> boardDtoList){
        return boardDtoList != null;
    }

    public List<CategoryDto> categoryDomainListToCategoryDtoList(List<Category> categoryList){
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for(Category category : categoryList){
            categoryDtoList.add(categoryDomainToCategoryDto(category));
        }
        return categoryDtoList;
    }

    public CategoryDto categoryDomainToCategoryDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .boardDtoList(boardInCategoryMapper.boardDomainListToBoardDtoList(category.getBoardList()))
                .build();
    }

    public Board boardDtoToBoardDomain(BoardDto boardDto){
        return boardInCategoryMapper.boardDtoToBoardDomain(boardDto);
    }

}
