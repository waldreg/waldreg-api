package org.waldreg.controller.board.board.request;

import javax.validation.constraints.NotBlank;

public class BoardCreateRequest{

    @NotBlank(message = "Board title cannot be null")
    private String title;
    private String content;

    private int categoryId;

    public BoardCreateRequest(){}

    private BoardCreateRequest(Builder builder){
        this.title = builder.title;
        this.content = builder.content;
        this.categoryId = builder.categoryId;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public final static class Builder{

        private String title;
        private String content;
        private int categoryId;

        {
            categoryId = 0;
        }

        private Builder(){}

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder categoryId(int categoryId){
            this.categoryId = categoryId;
            return this;
        }

        public BoardCreateRequest build(){
            return new BoardCreateRequest(this);
        }

    }

}