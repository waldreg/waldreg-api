package org.waldreg.domain.rewardtag;

import java.time.LocalDateTime;

public class RewardTagWrapper{

    private final int rewardId;
    private final LocalDateTime rewardPresentedAt;
    private final RewardTag rewardTag;

    private RewardTagWrapper(){
        throw new UnsupportedOperationException("Can not invoke constructor \"RewardTagWrapper()\"");
    }

    private RewardTagWrapper(Builder builder){
        this.rewardId = builder.rewardId;
        this.rewardPresentedAt = builder.rewardPresentedAt;
        this.rewardTag = builder.rewardTag;
    }

    public static Builder builder(){
        return new Builder();
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

        private int rewardId;
        private final LocalDateTime rewardPresentedAt;
        private RewardTag rewardTag;

        {
            rewardPresentedAt = LocalDateTime.now();
        }

        private Builder(){}

        public Builder rewardId(int rewardId){
            this.rewardId = rewardId;
            return this;
        }

        public Builder rewardTag(RewardTag rewardTag){
            this.rewardTag = rewardTag;
            return this;
        }

        public RewardTagWrapper build(){
            return new RewardTagWrapper(this);
        }

    }

}
