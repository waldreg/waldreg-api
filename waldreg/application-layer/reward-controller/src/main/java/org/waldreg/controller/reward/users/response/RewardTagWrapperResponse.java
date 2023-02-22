package org.waldreg.controller.reward.users.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class RewardTagWrapperResponse{

    @JsonProperty("reward_id")
    private int rewardId;

    @JsonProperty("reward_tag_id")
    private int rewardTagId;

    @JsonProperty("reward_tag_title")
    private String rewardTagTitle;

    @JsonProperty("reward_presented_at")
    private String rewardPresentedAt;

    @JsonProperty("reward_point")
    private int rewardPoint;

    public RewardTagWrapperResponse(){}

    private RewardTagWrapperResponse(Builder builder){
        this.rewardId = builder.rewardId;
        this.rewardTagId = builder.rewardTagId;
        this.rewardTagTitle = builder.rewardTagTitle;
        this.rewardPresentedAt = builder.rewardPresentedAt;
        this.rewardPoint = builder.rewardPoint;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getRewardId(){
        return rewardId;
    }

    public int getRewardTagId(){
        return rewardTagId;
    }

    public String getRewardTagTitle(){
        return rewardTagTitle;
    }

    public String getRewardPresentedAt(){
        return rewardPresentedAt;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public final static class Builder{

        private int rewardId;
        private int rewardTagId;
        private String rewardTagTitle;
        private String rewardPresentedAt;
        private int rewardPoint;

        private Builder(){}

        public Builder rewardId(int rewardId){
            this.rewardId = rewardId;
            return this;
        }

        public Builder rewardTagId(int rewardTagId){
            this.rewardTagId = rewardTagId;
            return this;
        }

        public Builder rewardTagTitle(String rewardTagTitle){
            this.rewardTagTitle = rewardTagTitle;
            return this;
        }

        public Builder rewardPresentedAt(String rewardPresentedAt){
            this.rewardPresentedAt = rewardPresentedAt;
            return this;
        }

        public Builder rewardPoint(int rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public RewardTagWrapperResponse build(){
            return new RewardTagWrapperResponse(this);
        }

    }

}