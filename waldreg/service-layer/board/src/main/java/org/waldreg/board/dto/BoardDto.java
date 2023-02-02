package org.waldreg.board.dto;

import java.time.LocalDateTime;
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
        this.imageUrls = builder.imageUrls;
        this.fileUrls = builder.fileUrls;
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

    public String getTitle(){
        return title;
    }

    public CategoryDto getCategory(){
        return category;
    }

    public String getContent(){
        return content;
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

    public List<String> getFileUrls(){
        return fileUrls;
    }

    public BoardServiceMemberTier getMemberTier(){
        return boardServiceMemberTier;
    }

    public void setBoardServiceMemberTier(BoardServiceMemberTier boardServiceMemberTier){
        this.boardServiceMemberTier = boardServiceMemberTier;
    }

    public List<String> getImageUrls(){
        return imageUrls;
    }

    public ReactionDto getReactions(){
        return reactions;
    }

    public List<CommentDto> getCommentList(){
        return commentList;
    }

    public int getViews(){
        return views;
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
        private List<String> fileUrls;
        private List<String> imageUrls;
        private ReactionDto reactions;
        private List<CommentDto> commentList;
        private int views;

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

        public Builder boardServiceMemberTier(BoardServiceMemberTier boardServiceMemberTier){
            this.boardServiceMemberTier = boardServiceMemberTier;
            return this;
        }

        public Builder imageUrls(List<String> imageUrls){
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder fileUrls(List<String> fileUrls){
            this.fileUrls = fileUrls;
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
