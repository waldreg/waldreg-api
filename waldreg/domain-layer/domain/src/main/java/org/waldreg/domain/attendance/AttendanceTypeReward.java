package org.waldreg.domain.attendance;

import org.waldreg.domain.rewardtag.RewardTag;

public final class AttendanceTypeReward{

    private final String name;
    private RewardTag rewardTag;

    private AttendanceTypeReward(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceTypeReward()\"");
    }

    public static Builder builder(){
        return new Builder();
    }

    private AttendanceTypeReward(Builder builder){
        this.name = builder.name;
        this.rewardTag = builder.rewardTag;
    }

    public String getName(){
        return name;
    }

    public void setRewardTag(RewardTag rewardTag){
        this.rewardTag = rewardTag;
    }

    public RewardTag getRewardTag(){
        return rewardTag;
    }

    public static final class Builder{

        private String name;
        private RewardTag rewardTag;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder rewardTag(RewardTag rewardTag){
            this.rewardTag = rewardTag;
            return this;
        }

        public AttendanceTypeReward build(){
            return new AttendanceTypeReward(this);
        }

    }

}