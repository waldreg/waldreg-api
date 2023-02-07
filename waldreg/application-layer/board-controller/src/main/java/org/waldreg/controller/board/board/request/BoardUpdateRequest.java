package org.waldreg.controller.board.board.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import javax.validation.constraints.NotBlank;

public class BoardUpdateRequest{

    @NotBlank(message = "BOARD-409 Blank board title")
    private String title;
    private String content;

    private int categoryId;
    @JsonProperty("delete_file_urls")
    private ArrayList<String> deleteFileUrls;

    public BoardUpdateRequest(){}
    private BoardUpdateRequest(Builder builder){
        this.title = builder.title;
        this.content = builder.content;
        this.categoryId = builder.categoryId;
        this.deleteFileUrls = builder.deleteFileUrls;
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

    public int getCategoryId(){
        return categoryId;
    }
    public ArrayList<String> getDeleteFileUrls(){
        return deleteFileUrls;
    }

    public final static class Builder{
        private String title;
        private String content;
        private int categoryId;
        private ArrayList<String>  deleteFileUrls;

        {
            categoryId = 0;
            deleteFileUrls = new ArrayList<>();
        }

        private Builder(){}

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
        public Builder deleteFileUrls( ArrayList<String> deleteFileUrls){
            this.deleteFileUrls = deleteFileUrls;
            return this;
        }
        public BoardUpdateRequest build(){
            return new BoardUpdateRequest(this);
        }

    }
}
