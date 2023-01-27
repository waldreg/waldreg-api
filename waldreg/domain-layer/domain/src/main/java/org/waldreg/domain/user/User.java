package org.waldreg.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTagWrapper;

@SuppressWarnings("unused")
public final class User{

    private int id;
    private String name;
    private final String userId;
    private String userPassword;
    private String phoneNumber;
    private final LocalDate createdAt;
    private final List<RewardTagWrapper> rewardTagWrapperList;
    private Character character;
    private final List<String> socialLogin;

    private User(){
        throw new UnsupportedOperationException("Can not invoke constructor \"User()\"");
    }

    private User(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
        this.createdAt = builder.createdAt;;
        this.rewardTagWrapperList = builder.rewardTagWrapperList;
        this.character = builder.character;
        this.socialLogin = builder.socialLogin;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public String getUserId(){
        return userId;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public List<RewardTagWrapper> getRewardTagWrapperList(){
        return rewardTagWrapperList;
    }

    public Character getCharacter(){
        return character;
    }

    public List<String> getSocialLogin(){
        return socialLogin;
    }

    public void setName(String name){this.name = name;}

    public void setUserPassword(String userPassword){this.userPassword = userPassword;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public void setCharacter(Character character){
        this.character = character;
    }

    public void addRewardTagWrapper(RewardTagWrapper rewardTagWrapper){
        this.rewardTagWrapperList.add(rewardTagWrapper);
    }

    public void deleteRewardTagWrapper(RewardTagWrapper rewardTagWrapper){
        for(RewardTagWrapper element : rewardTagWrapperList){
            if(element.getRewardId() == rewardTagWrapper.getRewardId()){
                rewardTagWrapperList.remove(element);
                break;
            }
        }
    }

    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private final LocalDate createdAt;
        private final List<RewardTagWrapper> rewardTagWrapperList;
        private List<String> socialLogin;

        {
            createdAt = LocalDate.now();
            rewardTagWrapperList = new ArrayList<>();
            socialLogin = new ArrayList<>();
        }

        private Character character;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder userPassword(String userPassword){
            this.userPassword = userPassword;
            return this;
        }

        public Builder phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder character(Character character){
            this.character = character;
            return this;
        }

        public Builder socialLogin(List<String> socialLogin){
            this.socialLogin = socialLogin;
            return this;
        }

        public User build(){
            return new User(this);
        }

    }

}
