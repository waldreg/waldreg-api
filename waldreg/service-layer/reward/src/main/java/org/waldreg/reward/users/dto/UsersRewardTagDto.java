package org.waldreg.reward.users.dto;

import java.time.LocalDateTime;

public class UsersRewardTagDto{

    private final int rewardId;
    private final int rewardTagId;
    private final String rewardTagTitle;
    private final LocalDateTime rewardPresentedAt;
    private final int rewardPoint;

    private UsersRewardTagDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"UsersRewardTagDto()\"");
    }

    private UsersRewardTagDto(Builder builder){
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

    public LocalDateTime getRewardPresentedAt(){
        return rewardPresentedAt;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public final static class Builder{

        private int rewardId;
        private int rewardTagId;
        private String rewardTagTitle;
        private LocalDateTime rewardPresentedAt;
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

        public Builder rewardPresentedAt(LocalDateTime rewardPresentedAt){
            this.rewardPresentedAt = rewardPresentedAt;
            return this;
        }

        public Builder rewardPoint(int rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public UsersRewardTagDto build(){
            return new UsersRewardTagDto(this);
        }

    }

}
