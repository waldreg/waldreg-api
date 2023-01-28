package org.waldreg.controller.board.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BoardListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;

    private BoardResponse[] boards;

    public BoardListResponse(){}

    private BoardListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.boards = builder.boards;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getMaxIdx(){
        return maxIdx;
    }

    public void setMaxIdx(int maxIdx){
        this.maxIdx = maxIdx;
    }

    public BoardResponse[] getBoards(){
        return boards;
    }

    public void setBoards(BoardResponse[] boards){
        this.boards = boards;
    }

    public final static class Builder{
        private int maxIdx;

        private BoardResponse[] boards;

        public Builder maxIdx(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }
        public Builder boards(BoardResponse[] boards){
            this.boards = boards;
            return this;
        }

        public BoardListResponse build(){
            return new BoardListResponse(this);
        }

    }

}
