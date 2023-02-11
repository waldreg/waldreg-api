package org.waldreg.controller.board.category.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.controller.board.category.response.CategoryListResponse;
import org.waldreg.controller.board.category.response.CategoryResponse;

@Service
public class ControllerCategoryMapper{

    public CategoryListResponse categoryListToCategoryListResponse(List<CategoryDto> categoryList){
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        for (CategoryDto categoryDto : categoryList){
            categoryResponseList.add(categoryDtoToCategoryResponse(categoryDto));
        }
        CategoryListResponse categoryListResponse = new CategoryListResponse(categoryResponseList.toArray(new CategoryResponse[categoryResponseList.size()]));
        return categoryListResponse;
    }

    private CategoryResponse categoryDtoToCategoryResponse(CategoryDto categoryDto){
        return CategoryResponse.builder()
                .categoryId(categoryDto.getId())
                .categoryName(categoryDto.getCategoryName())
                .categoryBoards(categoryDto.getBoardDtoList().size())
                .build();
    }

}
