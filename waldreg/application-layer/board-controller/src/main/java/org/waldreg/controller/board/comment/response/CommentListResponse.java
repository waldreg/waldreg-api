package org.waldreg.controller.board.comment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class CommentListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;

    private ArrayList<CommentResponse> commentResponses;

    public CommentListResponse(){}

    private CommentListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.commentResponses = builder.commentResponses;
    }

    public int getMaxIdx(){
        return maxIdx;
    }

    public ArrayList<CommentResponse> getComments(){
        return commentResponses;
    }

    public final static class Builder{

        private int maxIdx;

        private ArrayList<CommentResponse> commentResponses;

        public Builder maxIdx(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }

        public Builder comments(ArrayList comments){
            this.commentResponses = comments;
            return this;
        }

        public CommentListResponse build(){
            return new CommentListResponse(this);
        }

    }


}
