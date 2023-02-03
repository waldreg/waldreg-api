package org.waldreg.controller.board.response.author;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Author{

    @JsonProperty("user_id")
    private String userId;

    private String name;

    public Author(){}

    private Author(Builder builder){
        this.userId = builder.userId;
        this.name = builder.name;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public final static class Builder{
        private String userId;

        private String name;

        private Builder(){}

        public Builder userId(String userId){
            this.userId= userId;
            return this;
        }

        public Builder name(String name){
            this.name=name;
            return this;
        }

        public Author build(){
            return new Author(this);
        }

    }

}
