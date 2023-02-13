package org.waldreg.domain.attendance;

import org.waldreg.domain.rewardtag.RewardTag;

public final class AttendanceType{

    private final String name;
    private final RewardTag rewardTag;

    private AttendanceType(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceType()\"");
    }

    private AttendanceType(Builder builder){
        this.name = builder.name;
        this.rewardTag = builder.rewardTag;
    }

    public String getName(){
        return name;
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

        public AttendanceType build(){
            return new AttendanceType(this);
        }

    }

}
