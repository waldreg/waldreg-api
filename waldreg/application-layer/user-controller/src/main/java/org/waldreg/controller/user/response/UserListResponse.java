package org.waldreg.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserListResponse{

    @JsonProperty("max_idx")
    private final int maxIdx;
    @JsonProperty("users")
    private final List<UserResponse> userList;

    private UserListResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"UserListResponse()\"");
    }

    private UserListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.userList = builder.userList;
    }

    public static Builder builder(){return new Builder();}

    public int getMaxIdx(){return maxIdx;}

    public List<UserResponse> getUserList(){return userList;}

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
