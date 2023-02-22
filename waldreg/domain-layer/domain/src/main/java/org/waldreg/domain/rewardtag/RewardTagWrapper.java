package org.waldreg.domain.rewardtag;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "REWARD_TAG_WRAPPER")
public final class RewardTagWrapper{

    @Id
    @GeneratedValue
    @Column(name = "REWARD_TAG_WRAPPER_REWARD_ID")
    private Integer rewardId;

    @Column(name = "REWARD_TAG_WRAPPER_REWARD_PRESENTED_AT", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime rewardPresentedAt;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REWARD_TAG_REWARD_TAG_ID")
    private RewardTag rewardTag;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "REWARD_TAG_WRAPPER_USER_ID")
    private RewardTagWrapperUser rewardTagWrapperUser;

    private RewardTagWrapper(){}

    private RewardTagWrapper(Builder builder){
        this.rewardPresentedAt = builder.rewardPresentedAt;
        this.rewardTag = builder.rewardTag;
        this.rewardTagWrapperUser = builder.rewardTagWrapperUser;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getRewardId(){
        return rewardId;
    }

    public LocalDateTime getRewardPresentedAt(){
        return rewardPresentedAt;
    }

    public RewardTag getRewardTag(){
        return rewardTag;
    }

    public RewardTagWrapperUser getRewardTagWrapperUser(){
        return rewardTagWrapperUser;
    }

    public static final class Builder{

        private final LocalDateTime rewardPresentedAt;
        private RewardTag rewardTag;
        private RewardTagWrapperUser rewardTagWrapperUser;

        private Builder(){
            rewardPresentedAt = LocalDateTime.now();
        }

        public Builder rewardTag(RewardTag rewardTag){
            this.rewardTag = rewardTag;
            return this;
        }

        public Builder rewardTagWrapperUser(RewardTagWrapperUser rewardTagWrapperUser){
            this.rewardTagWrapperUser = rewardTagWrapperUser;
            return this;
        }

        public RewardTagWrapper build(){
            return new RewardTagWrapper(this);
        }

    }

}
