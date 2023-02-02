package org.waldreg.board.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BoardDto{

    private int id;
    private String title;
    private CategoryDto category;
    private String content;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private List<String> fileUrls;
    private List<String> imageUrls;
    private BoardServiceMemberTier boardServiceMemberTier;
    private ReactionDto reactions;
    private List<CommentDto> commentList;
    private int views;

    private BoardDto(){}

    private BoardDto(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.category = builder.category;
        this.content = builder.content;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.boardServiceMemberTier = builder.boardServiceMemberTier;
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

    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public CategoryDto getCategory(){
        return category;
    }

    public void setCategory(CategoryDto category){
        this.category = category;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public UserDto getUser(){
        return user;
    }

    public void setUser(UserDto user){
        this.user = user;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public BoardServiceMemberTier getMemberTier(){
        return boardServiceMemberTier;
    }

    public void setMemberTier(BoardServiceMemberTier boardServiceMemberTier){
        this.boardServiceMemberTier = boardServiceMemberTier;
    }

    public ReactionDto getReactions(){
        return reactions;
    }

    public void setReactions(ReactionDto reactions){
        this.reactions = reactions;
    }

    public List<CommentDto> getCommentList(){
        return commentList;
    }

    public void setCommentList(List<CommentDto> commentList){
        this.commentList = commentList;
    }

    public int getViews(){
        return views;
    }

    public void setViews(int views){
        this.views = views;
    }

    public static final class Builder{

        private int id;
        private String title;
        private CategoryDto category;
        private String content;
        private UserDto user;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private BoardServiceMemberTier boardServiceMemberTier;
        private ReactionDto reactions;
        private List<CommentDto> commentList;
        private int views;


        {
            createdAt = LocalDateTime.now();
            lastModifiedAt = createdAt;
            views = 0;
            commentList = new ArrayList<>();
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

        public Builder category(CategoryDto category){
            this.category = category;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder user(UserDto user){
            this.user = user;
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

        public Builder memberTier(BoardServiceMemberTier boardServiceMemberTier){
            this.boardServiceMemberTier = boardServiceMemberTier;
            return this;
        }

        public Builder reactions(ReactionDto reactions){
            this.reactions = reactions;
            return this;
        }

        public Builder views(int views){
            this.views = views;
            return this;
        }

        public Builder commentList(List<CommentDto> comments){
            this.commentList = commentList;
            return this;
        }

        public BoardDto build(){
            return new BoardDto(this);
        }

    }

}
