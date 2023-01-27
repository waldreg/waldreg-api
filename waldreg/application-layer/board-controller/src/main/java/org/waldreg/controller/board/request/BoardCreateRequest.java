package org.waldreg.controller.board.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class BoardCreateRequest{

    @NotBlank(message = "Can not be blank title")
    private String title;
    private String content;
    private String category;
    @JsonProperty("member_tier")
    private String memberTier;

    public BoardCreateRequest(){}
    private BoardCreateRequest(Builder builder){
        this.title = builder.title;
        this.content = builder.content;
        this.category = builder.category;
        this.memberTier = builder.memberTier;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public String getCategory(){
        return category;
    }

    public String getMemberTier(){
        return memberTier;
    }

    public final static class Builder{
        private String title;
        private String content;
        private String category;
        private String memberTier;

        private Builder(){};

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }
        public Builder category(String category){
            this.category = category;
            return this;
        }
        public Builder memberTier(String memberTier){
            this.memberTier = memberTier;
            return this;
        }
        public BoardCreateRequest build(){
            return new BoardCreateRequest(this);
        }

    }

}
