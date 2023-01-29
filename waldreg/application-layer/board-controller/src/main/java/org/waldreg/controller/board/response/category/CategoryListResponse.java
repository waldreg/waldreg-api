package org.waldreg.controller.board.response.category;

public class CategoryListResponse{

    private CategoryResponse[] categories;

    public CategoryListResponse(CategoryResponse[] categories){
        this.categories = categories;
    }

    public CategoryResponse[] getCategories(){
        return categories;
    }

}
