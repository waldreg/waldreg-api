package org.waldreg.controller.board.comment.request;

import javax.validation.constraints.NotBlank;
import org.waldreg.controller.board.category.request.CategoryRequest;
import org.waldreg.controller.board.category.request.CategoryRequest.Builder;

public class CommentRequest{

    @NotBlank(message = "BOARD-415 Blank comment")
    private String content;

    public CommentRequest(){}

    private CommentRequest(Builder builder){
        this.content = builder.content;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getContent(){
        return content;
    }

    public final static class Builder{

        private String content;


        public Builder content(String content){
            this.content = content;
            return this;
        }

        public CommentRequest build(){
            return new CommentRequest(this);
        }

    }


}
