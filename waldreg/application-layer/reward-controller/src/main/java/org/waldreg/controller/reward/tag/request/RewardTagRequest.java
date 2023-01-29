package org.waldreg.controller.reward.tag.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;

public final class RewardTagRequest{

    @NotNull(message = "Reward tag title cannot be null")
    @JsonProperty("reward_tag_title")
    private String rewardTagTitle;

    @NotNull(message = "Reward point cannot be null")
    @JsonProperty("reward_point")
    private int rewardPoint;

    public RewardTagRequest(){}

    private RewardTagRequest(Builder builder){
        this.rewardTagTitle = builder.rewardTagTitle;
        this.rewardPoint = builder.rewardPoint;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getRewardTagTitle(){
        return rewardTagTitle;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public void setRewardTagTitle(String rewardTagTitle){
        this.rewardTagTitle = rewardTagTitle;
    }

    public void setRewardPoint(int rewardPoint){
        this.rewardPoint = rewardPoint;
    }

    public final static class Builder{

        private String rewardTagTitle;
        private int rewardPoint;

        private Builder(){}

        public Builder rewardTagTitle(String rewardTagTitle){
            this.rewardTagTitle = rewardTagTitle;
            return this;
        }

        public Builder rewardPoint(int rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public RewardTagRequest build(){
            return new RewardTagRequest(this);
        }

    }

}