package org.waldreg.domain.attendance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.rewardtag.RewardTag;

@Entity
@Table(name = "ATTENDANCE_TYPE_REWARD")
public final class AttendanceTypeReward{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ATTENDANCE_TYPE_REWARD_ID")
    private Integer id;

    @Column(name = "ATTENDANCE_TYPE_NAME", nullable = false, unique = true)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REWARD_TAG_REWARD_TAG_ID")
    private RewardTag rewardTag;

    private AttendanceTypeReward(){}

    public static Builder builder(){
        return new Builder();
    }

    private AttendanceTypeReward(Builder builder){
        this.name = builder.name;
        this.rewardTag = builder.rewardTag;
    }

    public Integer getId(){
        return id;
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