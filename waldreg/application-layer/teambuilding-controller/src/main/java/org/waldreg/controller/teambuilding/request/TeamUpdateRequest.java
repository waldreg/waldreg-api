package org.waldreg.controller.teambuilding.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class TeamUpdateRequest{

    @NotBlank(message = "TEAMBUILDING-408 Team name cannot be blank")
    @JsonProperty("team_name")
    private String teamName;

    private TeamUpdateRequest(){}

    private TeamUpdateRequest(Builder builder){
        this.teamName = builder.teamName;
    }

    public static Builder builder(){return new Builder();}

    public String getTeamName(){
        return teamName;
    }

    public final static class Builder{

        private String teamName;

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public TeamUpdateRequest build(){return new TeamUpdateRequest(this);}

    }

}