package org.waldreg.teambuilding.teambuilding.dto;

import java.util.List;

public class TeamBuildingRequestDto{

    private String teamBuildingTitle;
    private int teamCount;
    private List<UserRequestDto> userRequestDtoList;

    private TeamBuildingRequestDto(){}

    private TeamBuildingRequestDto(Builder builder){
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.teamCount = builder.teamCount;
        this.userRequestDtoList = builder.userRequestDtoList;
    }

    public static Builder builder(){return new Builder();}

    public String getTeamBuildingTitle(){
        return teamBuildingTitle;
    }

    public int getTeamCount(){
        return teamCount;
    }

    public List<UserRequestDto> getUserList(){
        return userRequestDtoList;
    }

    public final static class Builder{

        private String teamBuildingTitle;
        private int teamCount;
        private List<UserRequestDto> userRequestDtoList;

        private Builder(){}

        public Builder teamBuildingTitle(String teamBuildingTitle){
            this.teamBuildingTitle = teamBuildingTitle;
            return this;
        }

        public Builder teamCount(int teamCount){
            this.teamCount = teamCount;
            return this;
        }

        public Builder userRequestDtoList(List<UserRequestDto> userRequestDtoList){
            this.userRequestDtoList = userRequestDtoList;
            return this;
        }

        public TeamBuildingRequestDto build(){return new TeamBuildingRequestDto(this);}

    }

}
