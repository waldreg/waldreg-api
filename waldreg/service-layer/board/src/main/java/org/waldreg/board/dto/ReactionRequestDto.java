package org.waldreg.board.dto;

public class ReactionRequestDto{

    private int boardId;
    private String userId;
    private String reactionType;

    private ReactionRequestDto(){}

    private ReactionRequestDto(Builder builder){
        this.boardId = builder.boardId;
        this.userId = builder.userId;
        this.reactionType = builder.reactionType;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getBoardId(){
        return boardId;
    }

    public String getUserId(){
        return userId;
    }

    public String getReactionType(){
        return reactionType;
    }

    public final static class Builder{

        private int boardId;
        private String userId;
        private String reactionType;

        public Builder boardId(int boardId){
            this.boardId = boardId;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder reactionType(String reactionType){
            this.reactionType = reactionType;
            return this;
        }

        public ReactionRequestDto build(){
            return new ReactionRequestDto(this);
        }

    }

}
