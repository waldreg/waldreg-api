package org.waldreg.controller.teambuilding.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MemberResponse{

    @JsonProperty("id")
    private int id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("name")
    private String name;

    private MemberResponse(){}

    private MemberResponse(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.name = builder.name;
    }

    public static Builder builder(){return new Builder();}

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public final static class Builder{

        private int id;
        private String userId;
        private String name;

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public MemberResponse build(){return new MemberResponse(this);}

    }

}