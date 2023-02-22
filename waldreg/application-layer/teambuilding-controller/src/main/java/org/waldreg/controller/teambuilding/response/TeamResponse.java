package org.waldreg.controller.teambuilding.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

public class TeamResponse{

    @JsonProperty("team_id")
    private int teamId;
    @JsonProperty("team_name")
    private String teamName;
    @JsonProperty("last_modified_at")
    private LocalDateTime lastModifiedAt;
    @JsonProperty("team_members")
    private List<UserResponse> memberList;

    private TeamResponse(){}

    private TeamResponse(Builder builder){
        this.teamId = builder.teamId;
        this.teamName = builder.teamName;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.memberList = builder.memberList;
    }

    public static Builder builder(){return new Builder();}

    public int getTeamId(){
        return teamId;
    }

    public String getTeamName(){
        return teamName;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public List<UserResponse> getMemberList(){
        return memberList;
    }

    public final static class Builder{

        private int teamId;
        private String teamName;
        private LocalDateTime lastModifiedAt;
        private List<UserResponse> memberList;

        public Builder teamId(int teamId){
            this.teamId = teamId;
            return this;
        }

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public Builder lastModified(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder memberList(List<UserResponse> memberList){
            this.memberList = memberList;
            return this;
        }

        public TeamResponse build(){return new TeamResponse(this);}

    }

}
