package org.waldreg.controller.teambuilding.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TeamBuildingUpdateRequest{

    @JsonProperty("teambuilding_title")
    private String teamBuildingTitle;

    private TeamBuildingUpdateRequest(){}

    private TeamBuildingUpdateRequest(Builder builder){
        this.teamBuildingTitle = builder.teamBuildingTitle;
    }

    public static Builder builder(){return new Builder();}

    public String getTeamBuildingTitle(){
        return teamBuildingTitle;
    }

    public final static class Builder{

        private String teamBuildingTitle;

        public Builder teamBuildingTitle(String teamBuildingTitle){
            this.teamBuildingTitle = teamBuildingTitle;
            return this;
        }

        public TeamBuildingUpdateRequest build(){return new TeamBuildingUpdateRequest(this);}

    }

}
