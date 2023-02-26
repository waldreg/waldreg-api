package org.waldreg.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.rewardtag.RewardTagWrapper;

@Entity
@Table(name = "USER")
public final class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTER_ID", nullable = false)
    private Character character;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RewardTagWrapper> rewardTagWrapperList;

    @Embedded
    private UserInfo userInfo;

    private User(){}

    private User(Builder builder){
        this.userInfo = builder.userInfo;
        this.rewardTagWrapperList = builder.rewardTagWrapperList;
        this.character = builder.character;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getName(){
        return userInfo.getName();
    }

    public String getUserId(){
        return userInfo.getUserId();
    }

    public String getUserPassword(){
        return userInfo.getUserPassword();
    }

    public String getPhoneNumber(){
        return userInfo.getPhoneNumber();
    }

    public LocalDate getCreatedAt(){
        return userInfo.getCreatedAt();
    }

    public Character getCharacter(){
        return character;
    }

    public List<RewardTagWrapper> getRewardTagWrapperList(){
        return rewardTagWrapperList;
    }

    public void setName(String name){
        this.userInfo.setName(name);
    }

    public void setUserPassword(String userPassword){
        this.userInfo.setUserPassword(userPassword);
    }

    public void setPhoneNumber(String phoneNumber){
        this.userInfo.setPhoneNumber(phoneNumber);
    }

    public void setCharacter(Character character){
        this.character = character;
    }

    public void addRewardTag(RewardTag rewardTag){
        RewardTagWrapper rewardTagWrapper = RewardTagWrapper.builder()
                .user(this)
                .rewardTag(rewardTag)
                .build();
        rewardTagWrapperList.add(rewardTagWrapper);
    }

    public void deleteRewardTag(int rewardId){
        rewardTagWrapperList.stream().filter(rw -> rw.getRewardId() == rewardId).findFirst().ifPresentOrElse(
                rw -> rewardTagWrapperList.remove(rw),
                () -> {throw new IllegalStateException("Cannot find rewardTagWrapper rewardId \"" + rewardId + "\"");}
        );
    }

    public static final class Builder extends UserInfo.Builder<User, Builder>{

        private Character character;
        private UserInfo userInfo;
        private List<RewardTagWrapper> rewardTagWrapperList;

        private Builder(){
            rewardTagWrapperList = new ArrayList<>();
        }

        public Builder character(Character character){
            this.character = character;
            return this;
        }

        public Builder rewardTagWrapperList(List<RewardTagWrapper> rewardTagWrapperList){
            this.rewardTagWrapperList = rewardTagWrapperList;
            return this;
        }

        @Override
        protected User abstractBuild(UserInfo userInfo){
            this.userInfo = userInfo;
            return new User(this);
        }

        @Override
        protected Builder builder(){
            return this;
        }

    }

}