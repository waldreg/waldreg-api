package org.waldreg.controller.board.category.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoryResponse{

    @JsonProperty("category_id")
    private int categoryId;
    @JsonProperty("category_name")
    private String categoryName;
    @JsonProperty("category_boards")
    private int categoryBoards;

    private CategoryResponse(){}

    private CategoryResponse(Builder builder){
        this.categoryId = builder.categoryId;
        this.categoryName = builder.categoryName;
        this.categoryBoards = builder.categoryBoards;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getCategoryId(){
        return categoryId;
    }

    public int getCategoryBoards(){
        return categoryBoards;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public final static class Builder{

        private int categoryId;
        private String categoryName;
        private int categoryBoards;

        private Builder(){}

        public Builder categoryId(int categoryId){
            this.categoryId = categoryId;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder categoryBoards(int categoryBoards){
            this.categoryBoards = categoryBoards;
            return this;
        }

        public CategoryResponse build(){
            return new CategoryResponse(this);
        }

    }

}
