package org.waldreg.domain.board.comment;

import java.time.LocalDateTime;
import org.waldreg.domain.user.User;

public final class Comment{

    private int id;
    private final int boardId;
    private final User user;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;
    private final String content;

    private Comment(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Comment()\"");
    }

    private Comment(Builder builder){
        this.id = builder.id;
        this.boardId = builder.boardId;
        this.user = builder.user;
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

    public void setId(int id){
        this.id = id;
    }

    public int getBoardId(){
        return boardId;
    }

    public User getUser(){
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

        private int boardId;
        private User user;
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

        public Builder boardId(int boardId){
            this.boardId = boardId;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Comment build(){
            return new Comment(this);
        }

    }

}
