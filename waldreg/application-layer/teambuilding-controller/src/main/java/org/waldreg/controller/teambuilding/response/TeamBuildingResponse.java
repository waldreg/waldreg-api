package org.waldreg.controller.teambuilding.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class TeamBuildingResponse{

    @JsonProperty("teambuilding_id")
    private int teamBuildingId;
    @JsonProperty("teambuilding_title")
    private String teamBuildingTitle;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedAt;
    @JsonProperty("teams")
    private List<TeamResponse> teamList;

    private TeamBuildingResponse(){}

    private TeamBuildingResponse(Builder builder){
        this.teamBuildingId = builder.teamBuildingId;
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.teamList = builder.teamList;
    }

    public static Builder builder(){return new Builder();}

    public int getTeamBuildingId(){
        return teamBuildingId;
    }

    public String getTeamBuildingTitle(){
        return teamBuildingTitle;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public List<TeamResponse> getTeamList(){
        return teamList;
    }

    public final static class Builder{

        private int teamBuildingId;
        private String teamBuildingTitle;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<TeamResponse> teamList;

        public Builder teamBuildingId(int teamBuildingId){
            this.teamBuildingId = teamBuildingId;
            return this;
        }

        public Builder teamBuildingTitle(String teamBuildingTitle){
            this.teamBuildingTitle = teamBuildingTitle;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt){
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder teamList(List<TeamResponse> teamList){
            this.teamList = teamList;
            return this;
        }

        public TeamBuildingResponse build(){return new TeamBuildingResponse(this);}

    }

}