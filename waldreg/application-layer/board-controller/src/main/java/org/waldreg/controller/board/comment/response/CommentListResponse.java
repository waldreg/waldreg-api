package org.waldreg.controller.board.comment.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class CommentListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;

    @JsonProperty("comments")
    private List<CommentResponse> commentResponseList;

    public CommentListResponse(){}

    private CommentListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.commentResponseList = builder.commentResponseList;
    }
    public static Builder builder(){
        return new Builder();
    }

    public int getMaxIdx(){
        return maxIdx;
    }

    public List<CommentResponse> getCommentResponseList(){
        return commentResponseList;
    }

    public final static class Builder{

        private int maxIdx;

        private List<CommentResponse> commentResponseList;

        public Builder maxIdx(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }

        public Builder commentResponseList(List<CommentResponse> commentResponseList){
            this.commentResponseList = commentResponseList;
            return this;
        }

        public CommentListResponse build(){
            return new CommentListResponse(this);
        }

    }


}
