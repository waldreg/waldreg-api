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
import org.waldreg.domain.user.User;

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
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    private RewardTagWrapper(){}

    private RewardTagWrapper(Builder builder){
        this.rewardPresentedAt = builder.rewardPresentedAt;
        this.rewardTag = builder.rewardTag;
        this.user = builder.user;
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

    public User getUser(){
        return user;
    }

    public static final class Builder{

        private final LocalDateTime rewardPresentedAt;
        private RewardTag rewardTag;
        private User user;

        private Builder(){
            rewardPresentedAt = LocalDateTime.now();
        }

        public Builder rewardTag(RewardTag rewardTag){
            this.rewardTag = rewardTag;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public RewardTagWrapper build(){
            return new RewardTagWrapper(this);
        }

    }

}
