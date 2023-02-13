package org.waldreg.domain.board.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.waldreg.domain.user.User;

public final class Reaction{

    private int boardId;

    private final Map<ReactionType, List<User>> reactionMap;

    private Reaction(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Reaction()\"");
    }

    private Reaction(Builder builder){
        this.boardId = builder.boardId;
        this.reactionMap = builder.reactionMap;
    }

    public int getBoardId(){
        return boardId;
    }

    public Map<ReactionType, List<User>> getReactionMap(){
        return reactionMap;
    }

    public static Builder builder(){
        return new Builder();
    }

    public final static class Builder{

        private int boardId;

        private Map<ReactionType, List<User>> reactionMap;

        {
            reactionMap = new HashMap<>();
            reactionMap.put(ReactionType.HEART, new ArrayList<>());
            reactionMap.put(ReactionType.BAD, new ArrayList<>());
            reactionMap.put(ReactionType.SAD, new ArrayList<>());
            reactionMap.put(ReactionType.SMILE, new ArrayList<>());
            reactionMap.put(ReactionType.GOOD, new ArrayList<>());
            reactionMap.put(ReactionType.CHECK, new ArrayList<>());
        }

        private Builder(){}

        public Builder boardId(int boardId){
            this.boardId = boardId;
            return this;
        }

        public Builder reactionMap(Map<ReactionType, List<User>> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public Reaction build(){
            return new Reaction(this);
        }

    }

}
