package org.waldreg.domain.board.reaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Reaction{

    private final Map<ReactionType, List<String>> reactionMap;

    private Reaction(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Reaction()\"");
    }

    private Reaction(Builder builder){
        this.reactionMap = builder.reactionMap;
    }

    public Map<ReactionType, List<String>> getReactionMap(){
        return reactionMap;
    }

    public static Builder builder(){
        return new Builder();
    }

    public final static class Builder{

        private Map<ReactionType, List<String>> reactionMap;

        {
            reactionMap = new HashMap<>();
        }

        private Builder(){}

        public Builder reactionMap(Map<ReactionType, List<String>> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public Reaction build(){
            return new Reaction(this);
        }

    }

}
