package org.waldreg.controller.teambuilding.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class TeamRequest{

    @JsonProperty("team_name")
    private String teamName;
    @JsonProperty("members")
    private List<String> members;

    private TeamRequest(){}

    private TeamRequest(Builder builder){
        this.teamName = builder.teamName;
        this.members = builder.members;
    }

    public static Builder builder(){return new Builder();}

    public String getTeamName(){
        return teamName;
    }

    public List<String> getMembers(){
        return members;
    }

    public final static class Builder{

        private String teamName;
        private List<String> members;

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public Builder members(List<String> members){
            this.members = members;
            return this;
        }

        public TeamRequest build(){return new TeamRequest(this);}

    }

}
