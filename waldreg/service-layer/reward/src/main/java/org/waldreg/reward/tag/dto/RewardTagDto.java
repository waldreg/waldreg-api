package org.waldreg.reward.tag.dto;

public final class RewardTagDto{

    private final int rewardTagId;
    private final String rewardTagTitle;
    private final int rewardPoint;

    private RewardTagDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"RewardTagDto()\"");
    }

    private RewardTagDto(Builder builder){
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

        public RewardTagDto build(){
            return new RewardTagDto(this);
        }

    }

}