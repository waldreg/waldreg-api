package org.waldreg.controller.teambuilding.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserWeightRequest{

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("weight")
    private int weight;

    private UserWeightRequest(){}

    private UserWeightRequest(Builder builder){
        this.userId = builder.userId;
        this.weight = builder.weight;
    }

    public static Builder builder(){return new Builder();}

    public String getUserId(){
        return userId;
    }

    public int getWeight(){
        return weight;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    public final static class Builder{

        private String userId;
        private int weight;

        private Builder(){}

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder weight(int weight){
            this.weight = weight;
            return this;
        }

        public UserWeightRequest build(){return new UserWeightRequest(this);}

    }

}
