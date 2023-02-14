package org.waldreg.board.dto;

public class UserDto{

    private int id;
    private String name;
    private String userId;

    private UserDto(){}

    private UserDto(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
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

    public void setName(String name){
        this.name = name;
    }

    public String getUserId(){
        return userId;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public final static class Builder{

        private int id;
        private String name;
        private String userId;

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

        public UserDto build(){
            return new UserDto(this);
        }

    }

}

