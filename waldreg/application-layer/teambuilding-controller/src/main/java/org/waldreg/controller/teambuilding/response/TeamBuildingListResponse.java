package org.waldreg.controller.teambuilding.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TeamBuildingListResponse{

    @JsonProperty("max_idx")
    private int maxIdx;
    @JsonProperty("teambuildings")
    private List<TeamBuildingResponse> teamBuildingList;

    private TeamBuildingListResponse(){}

    private TeamBuildingListResponse(Builder builder){
        this.maxIdx = builder.maxIdx;
        this.teamBuildingList = builder.teamBuildingList;
    }

    public static Builder builder(){return new Builder();}

    public int getMaxIdx(){
        return maxIdx;
    }

    public List<TeamBuildingResponse> getTeamBuildingList(){
        return teamBuildingList;
    }

    public final static class Builder{

        private int maxIdx;
        private List<TeamBuildingResponse> teamBuildingList;

        private Builder(){}

        public Builder maxIdx(int maxIdx){
            this.maxIdx = maxIdx;
            return this;
        }

        public Builder teamBuildingList(List<TeamBuildingResponse> teamBuildingList){
            this.teamBuildingList = teamBuildingList;
            return this;
        }

        public TeamBuildingListResponse build(){return new TeamBuildingListResponse(this);}

    }

}
