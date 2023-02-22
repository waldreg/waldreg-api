package org.waldreg.domain.rewardtag;

import java.time.LocalDateTime;

public class RewardTagWrapper{

    private int rewardId;
    private final LocalDateTime rewardPresentedAt;
    private final RewardTag rewardTag;

    private RewardTagWrapper(){
        throw new UnsupportedOperationException("Can not invoke constructor \"RewardTagWrapper()\"");
    }

    private RewardTagWrapper(Builder builder){
        this.rewardPresentedAt = builder.rewardPresentedAt;
        this.rewardTag = builder.rewardTag;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setRewardId(int rewardId){
        this.rewardId = rewardId;
    }

    public int getRewardId(){
        return rewardId;
    }

    public LocalDateTime getRewardPresentedAt(){
        return rewardPresentedAt;
    }

    public RewardTag getRewardTag(){
        return rewardTag;
    }

    public final static class Builder{

        private final LocalDateTime rewardPresentedAt;
        private RewardTag rewardTag;

        {
            rewardPresentedAt = LocalDateTime.now();
        }

        private Builder(){}

        public Builder rewardTag(RewardTag rewardTag){
            this.rewardTag = rewardTag;
            return this;
        }

        public RewardTagWrapper build(){
            return new RewardTagWrapper(this);
        }

    }

}
