package org.waldreg.board.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionDto{

    private Map<ReactionType, List<UserDto>> reactionMap;

    private ReactionDto(){}

    private ReactionDto(Builder builder){
        this.reactionMap = builder.reactionMap;
    }

    public Map<ReactionType, List<UserDto>> getReactionMap(){
        return reactionMap;
    }

    public final static class Builder{

        private Map<ReactionType, List<UserDto>> reactionMap;

        {
            reactionMap = new HashMap<>();
        }

        private Builder(){}

        public Builder reactionMap(Map<ReactionType, List<UserDto>> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public ReactionDto build(){
            return new ReactionDto(this);
        }

    }

}
