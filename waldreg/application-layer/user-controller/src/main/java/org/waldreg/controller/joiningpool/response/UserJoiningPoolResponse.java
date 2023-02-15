package org.waldreg.controller.joiningpool.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserJoiningPoolResponse{

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

    public UserJoiningPoolResponse(){}

    private UserJoiningPoolResponse(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
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


    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;

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

        public UserJoiningPoolResponse build(){
            return new UserJoiningPoolResponse(this);
        }

    }

}
