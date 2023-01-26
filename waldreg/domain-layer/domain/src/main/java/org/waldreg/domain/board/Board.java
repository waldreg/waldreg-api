package org.waldreg.domain.board;

import java.time.LocalDateTime;
import java.util.List;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.category.Category;
import org.waldreg.domain.tier.MemberTier;
import org.waldreg.domain.user.User;

public final class Board{

    private final int id;
    private final String title;
    private final Category category;
    private final String content;
    private final User user;
    private final LocalDateTime createdAt;
    private final LocalDateTime lastModifiedAt;
    private final MemberTier memberTier;
    private final List<String> imagePathList;
    private final List<String> filePathList;
    private final Reaction reactions;

    private Board(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Board()\"");
    }

    private Board(Builder builder){
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

    public Category getCategory(){
        return category;
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

    public MemberTier getMemberTier(){
        return memberTier;
    }

    public List<String> getImagePathList(){
        return imagePathList;
    }

    public List<String> getFilePathList(){
        return filePathList;
    }

    public Reaction getReactions(){
        return reactions;
    }

    public static final class Builder{

        private int id;
        private String title;
        private Category category;
        private String content;
        private User user;
        private final LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private MemberTier memberTier;
        private List<String> imagePathList;
        private List<String> filePathList;
        private Reaction reactions;

        {
            createdAt = LocalDateTime.now();
            lastModifiedAt = createdAt;
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

        public Builder category(Category category){
            this.category = category;
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

        public Builder reactions(Reaction reactions){
            this.reactions = reactions;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

    }

}