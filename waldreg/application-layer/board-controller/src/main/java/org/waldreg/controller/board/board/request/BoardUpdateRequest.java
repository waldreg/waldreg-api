package org.waldreg.controller.board.board.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import javax.validation.constraints.NotBlank;

public class BoardUpdateRequest{

    @NotBlank(message = "Can not be blank title")
    private String title;
    private String content;

    private int categoryId;
    @JsonProperty("member_tier")
    private String memberTier;

    @JsonProperty("delete_image_id")
    private ArrayList<Integer> deleteImageIdList;
    @JsonProperty("delete_file_id")
    private ArrayList<Integer> deleteFileIdList;

    public BoardUpdateRequest(){}
    private BoardUpdateRequest(Builder builder){
        this.title = builder.title;
        this.content = builder.content;
        this.categoryId = builder.categoryId;
        this.memberTier = builder.memberTier;
        this.deleteImageIdList = builder.deleteImageIdList;
        this.deleteFileIdList = builder.deleteFileIdList;
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

    public String getMemberTier(){
        return memberTier;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public ArrayList<Integer> getDeleteImageId(){
        return deleteImageIdList;
    }

    public ArrayList<Integer> getDeleteFileId(){
        return deleteFileIdList;
    }

    public final static class Builder{
        private String title;
        private String content;
        private int categoryId;
        private String memberTier;

        private ArrayList<Integer> deleteImageIdList;
        private ArrayList<Integer>  deleteFileIdList;

        {
            categoryId = 0;
            deleteImageIdList = new ArrayList<>();
            deleteFileIdList = new ArrayList<>();
        }

        private Builder(){};

        public Builder title(String title){
            this.title = title;
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }
        public Builder categoryId(int categoryId){
            this.categoryId = categoryId;
            return this;
        }
        public Builder memberTier(String memberTier){
            this.memberTier = memberTier;
            return this;
        }
        public Builder deleteImageIdList( ArrayList<Integer> deleteImageIdList){
            this.deleteImageIdList = deleteImageIdList;
            return this;
        }
        public Builder deleteFileIdList( ArrayList<Integer> deleteFileIdList){
            this.deleteFileIdList = deleteFileIdList;
            return this;
        }
        public BoardUpdateRequest build(){
            return new BoardUpdateRequest(this);
        }

    }
}
