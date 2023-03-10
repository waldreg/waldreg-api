package org.waldreg.user.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class UserDto{

    private final int id;
    private final String name;
    private final String userId;
    private final String userPassword;
    private final String phoneNumber;
    private final LocalDate createdAt;
    private final int rewardPoint;
    private String character;

    private UserDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"UserDto()\"");
    }

    public static Builder builder(){return new Builder();}

    private UserDto(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
        this.createdAt = builder.createdAt;
        this.rewardPoint = builder.rewardPoint;
        this.character = builder.character;
    }

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

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    public int getRewardPoint(){
        return rewardPoint;
    }

    public String getCharacter(){
        return character;
    }

    public void setCharacter(String character){
        this.character = character;
    }


    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private LocalDate createdAt;
        private int rewardPoint;
        private String character;

        {
            createdAt = LocalDate.now();
            rewardPoint = 0;
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

        public UserDto build(){
            return new UserDto(this);
        }

    }

}
