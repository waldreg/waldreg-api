package org.waldreg.auth.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import org.waldreg.auth.response.AuthTokenResponse;

public class AuthTokenRequest{

    @NotBlank(message = "user_id cannot be blank")
    @JsonProperty("user_id")
    private String userId;

    @NotBlank(message = "user_password cannot be blank")
    @JsonProperty("user_password")
    private String userPassword;

    public AuthTokenRequest(){

    }

    private AuthTokenRequest(Builder builder){
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getUserId(){
        return userId;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public void setUserId(String userId){
        this.userId = userId;
    }

    public void setUserPassword(String userPassword){
        this.userPassword = userPassword;
    }

    public static final class Builder{
        private String userId;
        private String userPassword;

        private Builder(){}

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder userPassword(String userPassword){
            this.userPassword = userPassword;
            return this;
        }

        public AuthTokenRequest build(){
            return new AuthTokenRequest(this);
        }
    }

}
