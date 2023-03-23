package org.waldreg.token.dto;

public class TokenUserDto{

    private final int id;
    private final String userId;
    private final String userPassword;

    private TokenUserDto(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public final static class Builder{

        private int id;
        private String userId;
        private String userPassword;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
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

        public TokenUserDto build(){
            return new TokenUserDto(this);
        }

    }

}
