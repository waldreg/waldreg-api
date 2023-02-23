package org.waldreg.domain.user;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.waldreg.domain.character.Character;

@Entity
@Table(name = "USER")
public final class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHARACTER_ID", nullable = false)
    private Character character;

    @Embedded
    private UserInfo userInfo;

    private User(){}

    private User(Builder builder){
        this.userInfo = builder.userInfo;
        this.character = builder.character;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getId(){
        return id;
    }

    public String getName(){
        return userInfo.getName();
    }

    public String getUserId(){
        return userInfo.getUserId();
    }

    public String getUserPassword(){
        return userInfo.getUserPassword();
    }

    public String getPhoneNumber(){
        return userInfo.getPhoneNumber();
    }

    public LocalDate getCreatedAt(){
        return userInfo.getCreatedAt();
    }

    public Character getCharacter(){
        return character;
    }

    public void setName(String name){
        this.userInfo.setName(name);
    }

    public void setUserPassword(String userPassword){
        this.userInfo.setUserPassword(userPassword);
    }

    public void setPhoneNumber(String phoneNumber){
        this.userInfo.setPhoneNumber(phoneNumber);
    }

    public void setCharacter(Character character){
        this.character = character;
    }

    public static final class Builder extends UserInfo.Builder<User, Builder>{

        private Character character;
        private UserInfo userInfo;

        private Builder(){}

        public Builder character(Character character){
            this.character = character;
            return this;
        }

        @Override
        protected User abstractBuild(UserInfo userInfo){
            this.userInfo = userInfo;
            return new User(this);
        }

        @Override
        protected Builder builder(){
            return this;
        }

    }

}
