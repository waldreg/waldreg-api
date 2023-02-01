package org.waldreg.controller.board.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class CategoryRequest{

    @NotBlank(message = "category title cannot be null")
    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("member_tier")
    private String memberTier;

    public CategoryRequest(){}

    private CategoryRequest(Builder builder){
        this.categoryName = builder.categoryName;
        this.memberTier = builder.memberTier;
    }

    public static Builder builder(){
        return new Builder();
    }


    public String getCategoryName(){
        return categoryName;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getMemberTier(){
        return memberTier;
    }

    public void setMemberTier(String memberTier){
        this.memberTier = memberTier;
    }

    public final static class Builder{

        private String categoryName;

        private String memberTier;

        private Builder(){}

        public Builder categoryName(String categoryName){
            this.categoryName = categoryName;
            return this;
        }

        public Builder memberTier(String memberTier){
            this.memberTier = memberTier;
            return this;
        }

        public CategoryRequest build(){
            return new CategoryRequest(this);
        }

    }


}
