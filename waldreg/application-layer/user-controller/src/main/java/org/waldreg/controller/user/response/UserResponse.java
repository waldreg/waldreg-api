package org.waldreg.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserResponse{

    @JsonProperty("id")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_password")
    private String userPassword;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("created_at")
    private LocalDate createdAt;
    @JsonProperty("character")
    private String character;
    @JsonProperty("reward_point")
    private int rewardPoint;
    @JsonProperty("social_login")
    private List<String> socialLogin;

    {
        socialLogin = new ArrayList<>();
    }


    public UserResponse(){}

    private UserResponse(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.createdAt = builder.createdAt;
        this.phoneNumber = builder.phoneNumber;
        this.character = builder.character;
        this.rewardPoint = builder.rewardPoint;
        this.socialLogin = builder.socialLogin;
    }

    public static Builder builder(){return new Builder();}

    public int getId(){
        return id;
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

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setUserPassword(String userPassword){
        this.userPassword = userPassword;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public String getCharacter(){
        return character;
    }

    public void setCharacter(String character){
        this.character = character;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public void setRewardPoint(int advantage){
        this.rewardPoint = rewardPoint;
    }

    public List<String> getSocialLogin(){
        return socialLogin;
    }

    public void setSocialLogin(ArrayList<String> socialLogin){
        this.socialLogin = socialLogin;
    }

    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private LocalDate createdAt;
        private String character;
        private int rewardPoint;
        private List<String> socialLogin;

        {
            socialLogin = new ArrayList<>();
        }

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

        public Builder createdAt(LocalDate createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder character(String character){
            this.character = character;
            return this;
        }

        public Builder rewardPoint(int rewardPoint){
            this.rewardPoint = rewardPoint;
            return this;
        }

        public Builder socialLogin(List<String> socialLogin){
            this.socialLogin = socialLogin;
            return this;
        }

        public UserResponse build(){
            return new UserResponse(this);
        }

    }

}