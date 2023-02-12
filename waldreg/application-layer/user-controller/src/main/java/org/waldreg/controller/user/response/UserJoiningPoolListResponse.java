package org.waldreg.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserJoiningPoolListResponse{


    @JsonProperty("max_idx")
    private int maxIdx;
    @JsonProperty("users")
    private List<UserJoiningPoolResponse> userList;

    public UserJoiningPoolListResponse(){}

    private UserJoiningPoolListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.userList = builder.userList;
    }

    public static Builder builder(){return new Builder();}

    public int getMaxIdx(){return maxIdx;}

    public List<UserJoiningPoolResponse> getUserList(){return userList;}

    public void setMaxIdx(int maxIdx){this.maxIdx = maxIdx;}

    public void setUserList(List<UserJoiningPoolResponse> userList){this.userList = userList;}


    public final static class Builder{

        private int maxIdx;
        private List<UserJoiningPoolResponse> userList;

        private Builder(){}

        public Builder maxId(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }

        public Builder userList(List<UserJoiningPoolResponse> userList){
            this.userList = userList;
            return this;
        }

        public UserJoiningPoolListResponse build(){return new UserJoiningPoolListResponse(this);}

    }


}
