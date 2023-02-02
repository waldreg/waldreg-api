package org.waldreg.controller.board.response.board;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import org.waldreg.controller.board.response.Reaction;
import org.waldreg.controller.board.response.author.Author;

public class BoardResponse{

    private int id;
    private String category;
    private String title;
    private String content;
    private Author author;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedAt;

    @JsonProperty("member_tier")
    private String memberTier;

    private String[] images;

    @JsonProperty("exist_file")
    private String existFile;

    private String[] files;

    private Reaction reaction;

    public BoardResponse(){}

    private BoardResponse(Builder builder){
        this.id = builder.id;
        this.category = builder.category;
        this.title = builder.title;
        this.content = builder.content;
        this.author = builder.author;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.memberTier = builder.memberTier;
        this.images = builder.images;
        this.existFile = builder.existFile;
        this.files = builder.files;
        this.reaction = builder.reaction;
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

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public Author getAuthor(){
        return author;
    }

    public void setAuthor(Author author){
        this.author = author;
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

    public String getMemberTier(){
        return memberTier;
    }

    public void setMemberTier(String memberTier){
        this.memberTier = memberTier;
    }

    public String[] getImages(){
        return images;
    }

    public void setImages(String[] images){
        this.images = images;
    }

    public String getExistFile(){
        return existFile;
    }

    public void setExistFile(String existFile){
        this.existFile = existFile;
    }

    public String[] getFiles(){
        return files;
    }

    public void setFiles(String[] files){
        this.files = files;
    }

    public Reaction getReaction(){
        return reaction;
    }

    public void setReaction(Reaction reaction){
        this.reaction = reaction;
    }

    public final static class Builder{

        private int id;
        private String category;
        private String title;
        private String content;
        private Author author;
        private LocalDateTime createdAt;

        private LocalDateTime lastModifiedAt;

        private String memberTier;

        private String[] images;
        private String existFile;

        private String[] files;

        private Reaction reaction;

        private Builder(){}


        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder category(String category){
            this.category = category;
            return this;
        }


        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder author(Author author){
            this.author = author;
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

        public Builder memberTier(String memberTier){
            this.memberTier = memberTier;
            return this;
        }

        public Builder images(String[] images){
            this.images = images;
            return this;
        }

        public Builder existFile(String existFile){
            this.existFile = existFile;
            return this;
        }

        public Builder files(String[] files){
            this.files = files;
            return this;
        }

        public Builder reaction(Reaction reaction){
            this.reaction = reaction;
            return this;
        }

        public BoardResponse build(){
            return new BoardResponse(this);
        }

    }


}

