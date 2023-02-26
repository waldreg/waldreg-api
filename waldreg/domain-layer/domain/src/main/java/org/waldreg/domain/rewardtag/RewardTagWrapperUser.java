package org.waldreg.domain.rewardtag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.user.User;

@Entity
@Table(name = "REWARD_TAG_WRAPPER_USER")
public final class RewardTagWrapperUser{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REWARD_TAG_WRAPPER_USER_ID")
    private Integer id;

    @OneToMany(mappedBy = "rewardTagWrapperUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<RewardTagWrapper> rewardTagWrapperList;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    private RewardTagWrapperUser(){ }

    private RewardTagWrapperUser(Builder builder){
        this.rewardTagWrapperList = builder.rewardTagWrapperList;
        this.user = builder.user;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public List<RewardTagWrapper> getRewardTagWrapperList(){
        return rewardTagWrapperList;
    }

    public User getUser(){
        return user;
    }

    public void deleteRewardTagWrapper(RewardTagWrapper rewardTagWrapper){
        for(RewardTagWrapper element : rewardTagWrapperList){
            if(Objects.equals(element.getRewardId(), rewardTagWrapper.getRewardId())){
                rewardTagWrapperList.remove(element);
                break;
            }
        }
    }

    public static final class Builder{

        private List<RewardTagWrapper> rewardTagWrapperList;
        private User user;

        private Builder(){
            rewardTagWrapperList = new ArrayList<>();
        }

        public Builder rewardTagWrapperList(List<RewardTagWrapper> rewardTagWrapperList){
            this.rewardTagWrapperList = rewardTagWrapperList;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public RewardTagWrapperUser build(){
            return new RewardTagWrapperUser(this);
        }

    }

}
