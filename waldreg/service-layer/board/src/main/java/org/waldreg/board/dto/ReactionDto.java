package org.waldreg.board.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionDto{

    private Map<BoardServiceReactionType, List<String>> reactionMap;

    private ReactionDto(){}

    private ReactionDto(Builder builder){
        this.reactionMap = builder.reactionMap;
    }

    public static Builder builder(){
        return new Builder();
    }
    public Map<BoardServiceReactionType, List<String>> getReactionMap(){
        return reactionMap;
    }

    public void setReactionMap(Map<BoardServiceReactionType, List<String>> reactionMap){
        this.reactionMap = reactionMap;
    }

    public final static class Builder{

        private Map<BoardServiceReactionType, List<String>> reactionMap;

        {
            reactionMap = new HashMap<>();
        }

        private Builder(){}

        public Builder reactionMap(Map<BoardServiceReactionType, List<String>> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public ReactionDto build(){
            return new ReactionDto(this);
        }

    }

}
