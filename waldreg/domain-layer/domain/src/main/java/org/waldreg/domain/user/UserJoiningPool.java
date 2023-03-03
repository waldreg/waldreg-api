package org.waldreg.domain.user;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "USER_JOINING_POOL")
public final class UserJoiningPool{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_JOINING_POOL_ID")
    private Integer id;

    @Embedded
    private UserInfo userInfo;

    private UserJoiningPool(){}

    private UserJoiningPool(Builder builder){
        this.userInfo = builder.userInfo;
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

    public static final class Builder extends UserInfo.Builder<UserJoiningPool, Builder>{

        private UserInfo userInfo;

        private Builder(){}

        @Override
        protected UserJoiningPool abstractBuild(UserInfo userInfo){
            this.userInfo = userInfo;
            return new UserJoiningPool(this);
        }

        @Override
        protected Builder builder(){
            return this;
        }

    }


}
