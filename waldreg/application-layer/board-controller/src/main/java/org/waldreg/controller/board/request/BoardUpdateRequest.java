package org.waldreg.controller.board.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import javax.validation.constraints.NotBlank;
import org.waldreg.controller.board.request.BoardCreateRequest.Builder;

public class BoardUpdateRequest{

    @NotBlank(message = "Can not be blank title")
    private String title;
    private String content;

    private int categoryId;
    @JsonProperty("member_tier")
    private String memberTier;

    @JsonProperty("delete_image_id")
    private ArrayList<Integer> deleteImageId;
    @JsonProperty("delete_file_id")
    private ArrayList<Integer> deleteFileId;

    public BoardUpdateRequest(){}
    private BoardUpdateRequest(Builder builder){
        this.title = builder.title;
        this.content = builder.content;
        this.categoryId = builder.categoryId;
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

    public String getMemberTier(){
        return memberTier;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public ArrayList<Integer> getDeleteImageId(){
        return deleteImageId;
    }

    public ArrayList<Integer> getDeleteFileId(){
        return deleteFileId;
    }

    public final static class Builder{
        private String title;
        private String content;
        private int categoryId;
        private String memberTier;

        private ArrayList<Integer> deleteImageId;
        private ArrayList<Integer>  deleteFileId;

        {
            categoryId = 0;
            deleteImageId = new ArrayList<>();
            deleteFileId = new ArrayList<>();
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
        public Builder deleteImageId( ArrayList<Integer> deleteImageId){
            this.deleteImageId = deleteImageId;
            return this;
        }
        public Builder deleteFileId( ArrayList<Integer> deleteFileId){
            this.deleteFileId = deleteFileId;
            return this;
        }
        public BoardUpdateRequest build(){
            return new BoardUpdateRequest(this);
        }

    }
}
