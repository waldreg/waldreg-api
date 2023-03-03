package org.waldreg.board.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionDto{

    @Override
    public String toString(){
        return "ReactionDto{" +
                "boardId=" + boardId +
                ", reactionMap=" + reactionMap +
                '}';
    }

    private int boardId;
    private Map<BoardServiceReactionType, List<UserDto>> reactionMap;

    private ReactionDto(){}

    private ReactionDto(Builder builder){
        this.boardId = builder.boardId;
        this.reactionMap = builder.reactionMap;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getBoardId(){
        return boardId;
    }

    public Map<BoardServiceReactionType, List<UserDto>> getReactionMap(){
        return reactionMap;
    }

    public void setReactionMap(Map<BoardServiceReactionType, List<UserDto>> reactionMap){
        this.reactionMap = reactionMap;
    }

    public final static class Builder{

        private int boardId;
        private Map<BoardServiceReactionType, List<UserDto>> reactionMap;

        {
            reactionMap = new HashMap<>();
        }

        private Builder(){}

        public Builder boardId(int boardId){
            this.boardId = boardId;
            return this;
        }

        public Builder reactionMap(Map<BoardServiceReactionType, List<UserDto>> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public ReactionDto build(){
            return new ReactionDto(this);
        }

    }

}
