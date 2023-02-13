package org.waldreg.controller.board.comment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class CommentResponse{

    private int id;
    @JsonProperty("user_id")
    private String userId;
    private String name;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedAt;
    private String content;

    private CommentResponse(){}

    private CommentResponse(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.content = builder.content;
    }

    public static Builder builder(){
        return new Builder();
    }


    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
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
        private String userId;
        private String name;
        private LocalDateTime createdAt;
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

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
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

        public CommentResponse build(){
            return new CommentResponse(this);
        }

    }

}
