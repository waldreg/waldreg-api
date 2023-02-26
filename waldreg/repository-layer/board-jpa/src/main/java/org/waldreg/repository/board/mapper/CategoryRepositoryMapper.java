package org.waldreg.repository.board.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.board.category.Category;

@Component
public class CategoryRepositoryMapper{

    public Category categoryDtoToCategoryDomain(CategoryDto categoryDto){
        return Category.builder()
                .id(categoryDto.getId())
                .categoryName(categoryDto.getCategoryName())
                .build();
    }

    public List<CategoryDto> categoryDomainListToCategoryDtoList(List<Category> categoryList){
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (Category category : categoryList){
            categoryDtoList.add(categoryDomainToCategoryDto(category));
        }
        return categoryDtoList;
    }

    public CategoryDto categoryDomainToCategoryDto(Category category){
        return CategoryDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .boardCount(category.getBoardList().size())
                .build();
    }

}
