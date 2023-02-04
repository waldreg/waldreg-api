package org.waldreg.domain.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.user.User;

public final class Board{

    private int id;
    private String title;
    private int categoryId;
    private String content;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private List<String> imagePathList;
    private List<String> filePathList;
    private Reaction reactions;
    private List<Comment> commentList;
    private int views;

    private Board(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Board()\"");
    }

    private Board(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.categoryId = builder.categoryId;
        this.content = builder.content;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.imagePathList = builder.imagePathList;
        this.filePathList = builder.filePathList;
        this.reactions = builder.reactions;
        this.commentList = builder.commentList;
        this.views = builder.views;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){this.id = id;}

    public String getTitle(){
        return title;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public String getContent(){
        return content;
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

    public List<String> getImagePathList(){
        return imagePathList;
    }

    public List<String> getFilePathList(){
        return filePathList;
    }

    public Reaction getReactions(){return reactions;}

    public List<Comment> getCommentList(){
        return commentList;
    }

    public int getViews(){
        return views;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public void setImagePathList(List<String> imagePathList){
        this.imagePathList = imagePathList;
    }

    public void setFilePathList(List<String> filePathList){
        this.filePathList = filePathList;
    }

    public void setReactions(Reaction reactions){
        this.reactions = reactions;
    }

    public void setCommentList(List<Comment> commentList){
        this.commentList = commentList;
    }

    public void setViews(int views){
        this.views = views;
    }

    public static final class Builder{

        private int id;
        private String title;
        private int categoryId;
        private String content;
        private User user;
        private final LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<String> imagePathList;
        private List<String> filePathList;
        private Reaction reactions;
        private List<Comment> commentList;
        private int views;

        {
            createdAt = LocalDateTime.now();
            lastModifiedAt = createdAt;
            reactions = Reaction.builder().build();
            commentList = List.of();
            views = 0;
        }

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder categoryId(int categoryId){
            this.categoryId = categoryId;
            return this;
        }

        public Builder content(String content){
            this.content = content;
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


        public Builder imagePathList(List<String> imagePathList){
            this.imagePathList = imagePathList;
            return this;
        }

        public Builder filePathList(List<String> filePathList){
            this.filePathList = filePathList;
            return this;
        }

        public Builder reactions(Reaction reactions){
            this.reactions = reactions;
            return this;
        }

        public Builder commentList(List<Comment> commentList){
            this.commentList = commentList;
            return this;
        }

        public Builder views(int views){
            this.views = views;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

    }

}
