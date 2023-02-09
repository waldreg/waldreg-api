package org.waldreg.board.dto;

import java.util.List;

public class CategoryDto{

    private int id;
    private String categoryName;

    private List<BoardDto> boardDtoList;

    private CategoryDto(){}

    private CategoryDto(Builder builder){
        this.id = builder.id;
        this.categoryName = builder.categoryName;
        this.boardDtoList = builder.boardDtoList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public List<BoardDto> getBoardDtoList(){
        return boardDtoList;
    }

    public final static class Builder{

        private int id;
        private String categoryName;
        private List<BoardDto> boardDtoList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder boardDtoList(List<BoardDto> boardDtoList){
            this.boardDtoList = boardDtoList;
            return this;
        }

        public CategoryDto build(){
            return new CategoryDto(this);
        }

    }

}

