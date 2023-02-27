package org.waldreg.board.dto;

public class CategoryDto{

    private int id;
    private String categoryName;

    private Integer boardCount;

    private CategoryDto(){}

    private CategoryDto(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.boardCount = builder.boardCount;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public Integer getBoardCount(){
        return boardCount;
    }

    public final static class Builder{

        private int id;
        private String categoryName;
        private Integer boardCount;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder boardCount(Integer boardCount){
            this.boardCount = boardCount;
            return this;
        }

        public CategoryDto build(){
            return new CategoryDto(this);
        }

    }

}

