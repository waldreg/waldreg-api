package org.waldreg.controller.board.category.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class CategoryRequest{

    @NotBlank(message = "BOARD-411 Blank category name")
    @JsonProperty("category_name")
    private String categoryName;

    public CategoryRequest(){}

    private CategoryRequest(Builder builder){
        this.categoryName = builder.categoryName;
    }

    public static Builder builder(){
        return new Builder();
    }


    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public final static class Builder{

        private String categoryName;


        private Builder(){}

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public CategoryRequest build(){
            return new CategoryRequest(this);
        }

    }


}
