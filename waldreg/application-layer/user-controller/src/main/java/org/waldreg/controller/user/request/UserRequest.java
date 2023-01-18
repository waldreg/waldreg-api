package org.waldreg.controller.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UserRequest{

    @NotBlank(message = "Invalid name input")
    @JsonProperty("name")
    private String name;
    @NotBlank(message = "Invalid user_id input")
    @JsonProperty("user_id")
    private String userId;
    @NotBlank(message = "Invalid user_password input")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "Unsecured password")
    @JsonProperty("user_password")
    private String userPassword;
    @NotNull(message = "Invalid phone_number input")
    @Pattern(regexp = "^01(?:0|1|[6-9])[.-]?(\\d{3}|\\d{4})[.-]?(\\d{4})$", message = "Invalid phone_number input")
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("character")
    private String character;

    public UserRequest(){}

    private UserRequest(Builder builder){
        this.name = builder.name;
        this.userId = builder.userId;
        this.userPassword = builder.userPassword;
        this.phoneNumber = builder.phoneNumber;
        this.character = builder.character;
    }

    public static Builder builder(){return new Builder();}

    public String getName(){return name;}

    public String getUserId(){return userId;}

    public String getUserPassword(){return userPassword;}

    public String getPhoneNumber(){return phoneNumber;}

    public String getCharacter(){return character;}

    public void setName(String name){this.name = name;}

    public void setUserId(String userId){this.userId = userId;}

    public void setUserPassword(String userPassword){this.userPassword = userPassword;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public void setCharacter(String character){this.character = character;}

    public final static class Builder{

        private String name;
        private String userId;
        private String userPassword;
        private String phoneNumber;
        private String character;

        private Builder(){}

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

        public Builder character(String character){
            this.character = character;
            return this;
        }

        public UserRequest build(){return new UserRequest(this);}

    }

}