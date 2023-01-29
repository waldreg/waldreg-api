package org.waldreg.controller.reward.users.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class UsersRewardTagResponse{

    private final int id;
    private final String name;

    @JsonProperty("user_id")
    private final String userId;
    private final int reward;

    @JsonProperty("reward_infos")
    private final List<RewardTagWrapperResponse> rewardInfoList;

    private UsersRewardTagResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"UsersRewardTagResponse()\"");
    }

    private UsersRewardTagResponse(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.reward = builder.reward;
        this.rewardInfoList = builder.rewardInfoList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getUserId(){
        return userId;
    }

    public int getReward(){
        return reward;
    }

    public List<RewardTagWrapperResponse> getRewardInfoList(){
        return rewardInfoList;
    }

    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private int reward;
        private List<RewardTagWrapperResponse> rewardInfoList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder reward(int reward){
            this.reward = reward;
            return this;
        }

        public Builder rewardInfoList(List<RewardTagWrapperResponse> rewardInfoList){
            this.rewardInfoList = rewardInfoList;
            return this;
        }

        public UsersRewardTagResponse build(){
            return new UsersRewardTagResponse(this);
        }

    }

}