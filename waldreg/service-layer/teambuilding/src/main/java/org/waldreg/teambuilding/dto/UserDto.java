package org.waldreg.teambuilding.dto;

public class UserDto{

    private int id;
    private String userId;
    private String name;

    private UserDto(){}

    private UserDto(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.name = builder.name;
    }

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getName(){
        return name;
    }

    public static Builder builder(){return new Builder();}

    public final static class Builder{

        private int id;
        private String userId;
        private String name;

        private Builder(){}

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

        public UserDto build(){return new UserDto(this);}

    }

}
