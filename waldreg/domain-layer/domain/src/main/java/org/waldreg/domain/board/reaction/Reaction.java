package org.waldreg.domain.board.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.waldreg.domain.user.User;

public final class Reaction{

    private final Map<ReactionType, Integer> reactionMap;

    private final List<User> userList;

    private Reaction(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Reaction()\"");
    }

    private Reaction(Builder builder){
        this.reactionMap = builder.reactionMap;
        this.userList = builder.userList;
    }

    public Map<ReactionType, Integer> getReactionMap(){
        return reactionMap;
    }

    public List<User> getUserList(){
        return userList;
    }

    public final static class Builder{

        private Map<ReactionType, Integer> reactionMap;

        private List<User> userList;

        {
            reactionMap = new HashMap<>();
            userList = new ArrayList<>();
        }

        private Builder(){}

        public Builder reactionMap(Map<ReactionType, Integer> reactionMap){
            this.reactionMap = reactionMap;
            return this;
        }

        public Builder userList(List<User> userList){
            this.userList = userList;
            return this;
        }

        public Reaction build(){
            return new Reaction(this);
        }

    }

}
