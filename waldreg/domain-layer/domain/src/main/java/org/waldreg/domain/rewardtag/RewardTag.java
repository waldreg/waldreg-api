package org.waldreg.domain.rewardtag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "REWARD_TAG")
public final class RewardTag{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REWARD_TAG_REWARD_TAG_ID")
    private Integer rewardTagId;

    @Column(name = "REWARD_TAG_REWARD_TAG_TITLE", nullable = false, length = 105)
    private String rewardTagTitle;

    @Column(name = "REWARD_TAG_REWARD_POINT", nullable = false)
    private Integer rewardPoint;

    private RewardTag(){}

    private RewardTag(Builder builder){
        this.rewardTagTitle = builder.rewardTagTitle;
        this.rewardPoint = builder.rewardPoint;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getRewardTagId(){
        return rewardTagId;
    }

    public String getRewardTagTitle(){
        return rewardTagTitle;
    }

    public Integer getRewardPoint(){
        return rewardPoint;
    }

    public static final class Builder{

        private String rewardTagTitle;
        private Integer rewardPoint;

        private Builder(){}

        public Builder rewardTagTitle(String rewardTagTitle){
            this.rewardTagTitle = rewardTagTitle;
            return this;
        }

        public Builder rewardPoint(Integer rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public RewardTag build(){
            return new RewardTag(this);
        }

    }

}
