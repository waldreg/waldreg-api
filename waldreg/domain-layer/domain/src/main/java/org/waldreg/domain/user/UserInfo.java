package org.waldreg.domain.user;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import org.waldreg.domain.character.Character;

@Embeddable
public class UserInfo{

    @Column(name = "USER_NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "USER_USER_ID", nullable = false, length = 50, unique = true)
    private String userId;

    @Column(name = "USER_USER_PASSWORD", nullable = false, length = 20)
    private String userPassword;

    @Column(name = "USER_PHONE_NUMBER", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "USER_CREATED_AT", nullable = false, columnDefinition = "DATE")
    private LocalDate createdAt;

    private UserInfo(){}

    private UserInfo(Builder<?, ?> builder){
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
        this.createdAt = builder.createdAt;
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

    public void setName(String name){this.name = name;}

    public void setUserPassword(String userPassword){this.userPassword = userPassword;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public abstract static class Builder<T, P extends Builder<T, ?>>{

        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private final LocalDate createdAt;

        Builder(){
            createdAt = LocalDate.now();
        }

        public P name(String name){
            this.name = name;
            return builder();
        }

        public P userId(String userId){
            this.userId = userId;
            return builder();
        }

        public P userPassword(String userPassword){
            this.userPassword = userPassword;
            return builder();
        }

        public P phoneNumber(String phoneNumber){
            this.phoneNumber = phoneNumber;
            return builder();
        }

        protected abstract P builder();

        public T build(){
            UserInfo userInfo = createUserInfo();
            return abstractBuild(userInfo);
        }

        private UserInfo createUserInfo(){
            return new UserInfo(this);
        }

        protected abstract T abstractBuild(UserInfo userInfo);

    }


}
