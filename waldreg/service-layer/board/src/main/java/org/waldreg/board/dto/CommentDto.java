package org.waldreg.board.dto;

import java.time.LocalDateTime;

public class CommentDto{

    private int id;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private String content;

    private CommentDto(){}

    private CommentDto(Builder builder){
        this.id = builder.id;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.content = builder.content;
    }

    public int getId(){
        return id;
    }

    public UserDto getUser(){
        return user;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public String getContent(){
        return content;
    }

    public final static class Builder{

        private int id;
        private UserDto user;
        private final LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private String content;

        {
            createdAt = LocalDateTime.now();
            this.lastModifiedAt = createdAt;
        }

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder user(UserDto user){
            this.user = user;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public CommentDto build(){
            return new CommentDto(this);
        }

    }

}

