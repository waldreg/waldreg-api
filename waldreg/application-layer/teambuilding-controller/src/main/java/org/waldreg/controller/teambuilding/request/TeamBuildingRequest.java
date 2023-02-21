package org.waldreg.controller.teambuilding.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TeamBuildingRequest{

    @JsonProperty("teambuilding_title")
    private String teamBuildingTitle;
    @JsonProperty("team_count")
    private int teamCount;
    @JsonProperty("users")
    private List<UserWeightRequest> userList;

    private TeamBuildingRequest(){}

    private TeamBuildingRequest(Builder builder){
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.teamCount = builder.teamCount;
        this.userList = builder.userList;
    }

    public static Builder builder(){return new Builder();}

    public String getTeamBuildingTitle(){
        return teamBuildingTitle;
    }

    public int getTeamCount(){
        return teamCount;
    }

    public List<UserWeightRequest> getUserList(){
        return userList;
    }

    public final static class Builder{

        private String teamBuildingTitle;
        private int teamCount;
        private List<UserWeightRequest> userList;

        private Builder(){}

        public Builder teamBuildingTitle(String teamBuildingTitle){
            this.teamBuildingTitle = teamBuildingTitle;
            return this;
        }

        public Builder teamCount(int teamCount){
            this.teamCount = teamCount;
            return this;
        }

        public Builder userList(List<UserWeightRequest> userList){
            this.userList = userList;
            return this;
        }

        public TeamBuildingRequest build(){return new TeamBuildingRequest(this);}

    }

}
