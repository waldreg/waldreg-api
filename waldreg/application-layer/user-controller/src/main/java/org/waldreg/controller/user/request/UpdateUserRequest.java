package org.waldreg.controller.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UpdateUserRequest{

    @NotBlank(message = "USER-402 Invalid name input")
    @JsonProperty("name")
    private String name;

    @NotBlank(message = "USER-401 Unsecured user_password input")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "USER-401 Unsecured user_password input")
    @JsonProperty("user_password")
    private String userPassword;

    @NotNull(message = "USER-405 Invalid phone_number input")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "USER-405 Invalid phone_number input")
    @JsonProperty("phone_number")
    private String phoneNumber;

    public UpdateUserRequest(){}

    private UpdateUserRequest(Builder builder){
        this.name = builder.name;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
    }

    public static Builder builder(){return new Builder();}

    public String getName(){return name;}

    public String getUserPassword(){return userPassword;}

    public String getPhoneNumber(){return phoneNumber;}

    public void setName(String name){this.name = name;}

    public void setUserPassword(String userPassword){this.userPassword = userPassword;}

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public final static class Builder{

        private String name;
        private String userPassword;
        private String phoneNumber;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
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


        public UpdateUserRequest build(){return new UpdateUserRequest(this);}

    }

}