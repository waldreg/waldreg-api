package org.waldreg.domain.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.character.Character;

@SuppressWarnings("unused")
public final class User{

    private final int id;
    private final String name;
    private final String userId;
    private final String userPassword;
    private final String phoneNumber;
    private final LocalDate createdAt;
    private final int advantage;
    private final int penalty;
    private final Character character;
    private List<String> socialLogin;

    {
        socialLogin = new ArrayList<>();
    }

    private User(){
        throw new UnsupportedOperationException("Can not invoke constructor \"User()\"");
    }

    private User(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
        this.createdAt = builder.createdAt;
        this.advantage = builder.advantage;
        this.penalty = builder.penalty;
        this.character = builder.character;
        this.socialLogin = builder.socialLogin;
    }

    public static Builder builder(){
        return new Builder();
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

    public int getAdvantage(){
        return advantage;
    }

    public int getPenalty(){
        return penalty;
    }

    public Character getCharacter(){
        return character;
    }

    public List<String> getSocialLogin(){
        return socialLogin;
    }

    @SuppressWarnings("unused")
    public final static class Builder{

        private int id;
        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private final LocalDate createdAt;
        private final int advantage;
        private final int penalty;
        private List<String> socialLogin;

        {
            createdAt = LocalDate.now();
            advantage = 0;
            penalty = 0;
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
