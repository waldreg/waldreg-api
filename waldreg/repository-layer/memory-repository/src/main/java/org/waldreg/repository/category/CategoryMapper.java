package org.waldreg.repository.category;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.category.Category;

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

}
