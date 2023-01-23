package org.waldreg.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;
    @JsonProperty("users")
    private List<UserResponse> userList;

    public UserListResponse(){}

    private UserListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.userList = builder.userList;
    }

    public static Builder builder(){return new Builder();}

    public int getMaxIdx(){return maxIdx;}

    public List<UserResponse> getUserList(){return userList;}

    public void setMaxIdx(int maxIdx){this.maxIdx = maxIdx;}

    public void setUserList(List<UserResponse> userList){this.userList = userList;}


    public final static class Builder{

        private int maxIdx;
        private List<UserResponse> userList;

        private Builder(){}

        public Builder maxId(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }

        public Builder userList(List<UserResponse> userList){
            this.userList = userList;
            return this;
        }

        public UserListResponse build(){return new UserListResponse(this);}

    }

}
