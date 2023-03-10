package org.waldreg.controller.reward.tag.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class RewardTagResponse{

    @JsonProperty("reward_tag_id")
    private int rewardTagId;

    @JsonProperty("reward_tag_title")
    private String rewardTagTitle;

    @JsonProperty("reward_point")
    private int rewardPoint;

    private RewardTagResponse(){}

    private RewardTagResponse(Builder builder){
        this.rewardTagId = builder.rewardTagId;
        this.rewardTagTitle = builder.rewardTagTitle;
        this.rewardPoint = builder.rewardPoint;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getRewardTagId(){
        return rewardTagId;
    }

    public String getRewardTagTitle(){
        return rewardTagTitle;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public final static class Builder{

        private int rewardTagId;
        private String rewardTagTitle;
        private int rewardPoint;

        private Builder(){}

        public Builder rewardTagId(int rewardTagId){
            this.rewardTagId = rewardTagId;
            return this;
        }

        public Builder rewardTagTitle(String rewardTagTitle){
            this.rewardTagTitle = rewardTagTitle;
            return this;
        }

        public Builder rewardPoint(int rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public RewardTagResponse build(){
            return new RewardTagResponse(this);
        }

    }

}