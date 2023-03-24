package org.waldreg.controller.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SpecificUserListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;
    @JsonProperty("users")
    private List<UserResponse> userList;

    public SpecificUserListResponse(){}

    public SpecificUserListResponse(List<UserResponse> userResponseList){
        this.userList = userResponseList;
    }

    public List<UserResponse> getUserList(){return userList;}

    public void setUserList(List<UserResponse> userList){this.userList = userList;}

}
