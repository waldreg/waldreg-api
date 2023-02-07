package org.waldreg.controller.board.category.response;

public class CategoryListResponse{

    private CategoryResponse[] categories;

    public CategoryListResponse(){};

    public CategoryListResponse(CategoryResponse[] categories){
        this.categories = categories;
    }

    public CategoryResponse[] getCategories(){
        return categories;
    }

}
