package org.waldreg.board.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BoardDto{

    private int id;
    private String title;
    private CategoryDto categoryDto;
    private String content;
    private UserDto userDto;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private List<String> fileUrls;
    private List<String> imageUrls;
    private MemberTier memberTier;
    private ReactionDto reactions;
    private List<CommentDto> commentList;
    private int views;

    private BoardDto(){}

    private BoardDto(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.categoryDto = builder.categoryDto;
        this.content = builder.content;
        this.userDto = builder.userDto;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.imageUrls = builder.imageUrls;
        this.fileUrls = builder.fileUrls;
        this.memberTier = builder.memberTier;
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

    public CategoryDto getCategoryDto(){
        return categoryDto;
    }

    public String getContent(){
        return content;
    }

    public UserDto getUserDto(){
        return userDto;
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

    public List<String> getImageUrls(){
        return imageUrls;
    }

    public MemberTier getMemberTier(){
        return memberTier;
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
        private CategoryDto categoryDto;
        private String content;
        private UserDto userDto;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;

        private List<String> fileUrls;
        private List<String> imageUrls;
        private MemberTier memberTier;
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

        public Builder categoryDto(CategoryDto categoryDto){
            this.categoryDto = categoryDto;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder userDto(UserDto userDto){
            this.userDto = userDto;
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

        public Builder imageUrls(List<String> imageUrls){
            this.imageUrls = imageUrls;
            return this;
        }

        public Builder fileUrls(List<String> fileUrls){
            this.fileUrls = fileUrls;
            return this;
        }

        public Builder memberTier(MemberTier memberTier){
            this.memberTier = memberTier;
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

        public BoardDto build(){
            return new BoardDto(this);
        }

    }

}
