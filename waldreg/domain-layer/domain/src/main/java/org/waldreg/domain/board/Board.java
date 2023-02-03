package org.waldreg.domain.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.category.Category;
import org.waldreg.domain.user.User;

public final class Board{

    private int id;
    private final String title;
    private final int categoryId;
    private final String content;
    private User user;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;
    private final List<String> imagePathList;
    private final List<String> filePathList;
    private final Reaction reactions;
    private final List<Comment> commentList;
    private final int views;

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

    public void setUser(User user){this.user = user;}

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
            imagePathList = new ArrayList<>();
            filePathList = new ArrayList<>();
            reactions = Reaction.builder().build();
            commentList = new ArrayList<>();
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

        public Builder category(int categoryId){
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
