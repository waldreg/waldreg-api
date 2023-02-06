package org.waldreg.controller.board.category;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.board.category.management.CategoryManager;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.controller.board.category.mapper.ControllerCategoryMapper;
import org.waldreg.controller.board.category.request.CategoryRequest;
import org.waldreg.controller.board.category.response.CategoryListResponse;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class CategoryController{

    private CategoryManager categoryManager;

    private ControllerCategoryMapper controllerCategoryMapper;

    @Authenticating
    @PostMapping("/category")
    public void createCategory(@RequestBody CategoryRequest categoryRequest){
        CategoryDto categoryDto = CategoryDto.builder().categoryName(categoryRequest.getCategoryName()).build();
        categoryManager.createCategory(categoryDto);
    }

    @Authenticating
    @GetMapping("/category")
    public CategoryListResponse getCategoryList(){
        return controllerCategoryMapper.categoryListToCategoryListResponse(categoryManager.inquiryAllCategory());
    }

    @Authenticating
    @PutMapping("/category/{category-id}")
    public void updateCategory(@RequestBody CategoryRequest categoryRequest, @PathVariable("category-id") int categoryId){
        CategoryDto categoryDto = CategoryDto.builder()
                .id(categoryId)
                .categoryName(categoryRequest.getCategoryName())
                .build();
        categoryManager.modifyCategory(categoryDto);
    }

    @Authenticating
    @DeleteMapping("/category/{category-id}")
    public void deleteCategory(@PathVariable("category-id") int categoryId){
        categoryManager.deleteCategory(categoryId);
    }


}
