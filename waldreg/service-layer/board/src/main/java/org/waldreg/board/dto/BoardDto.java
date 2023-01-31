package org.waldreg.board.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BoardDto{
    private final int id;
    private final String title;
    private final CategoryDto category;
    private final String content;
    private final UserDto user;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;
    private final MemberTier memberTier;
    private final List<String> imagePathList;
    private final List<String> filePathList;
    private final ReactionDto reactions;
    private final List<CommentDto> commentList;
    private final int views;

    private BoardDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Board()\"");
    }

    private BoardDto(Builder builder){
        this.id = builder.id;
        this.title = builder.title;
        this.category = builder.category;
        this.content = builder.content;
        this.user = builder.user;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.memberTier = builder.memberTier;
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

    public MemberTier getMemberTier(){
        return memberTier;
    }

    public List<String> getImagePathList(){
        return imagePathList;
    }

    public List<String> getFilePathList(){
        return filePathList;
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
        private final LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private MemberTier memberTier;
        private List<String> imagePathList;
        private List<String> filePathList;
        private ReactionDto reactions;
        private List<CommentDto> commentList;
        private int views;


        {
            createdAt = LocalDateTime.now();
            lastModifiedAt = createdAt;
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

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder memberTier(MemberTier memberTier){
            this.memberTier = memberTier;
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

        public Builder reactions(ReactionDto reactions){
            this.reactions = reactions;
            return this;
        }

        public Builder commentList(List<CommentDto> commentList){
            this.commentList = commentList;
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
