package org.waldreg.domain.rewardtag;

public class RewardTag{

    private int rewardTagId;
    private final String rewardTagTitle;
    private final int rewardPoint;

    private RewardTag(){
        throw new UnsupportedOperationException("Can not invoke constructor \"RewardTag()\"");
    }

    private RewardTag(Builder builder){
        this.rewardTagTitle = builder.rewardTagTitle;
        this.rewardPoint = builder.rewardPoint;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setRewardTagId(int rewardTagId){
        this.rewardTagId = rewardTagId;
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

        public RewardTag build(){
            return new RewardTag(this);
        }

    }

}
