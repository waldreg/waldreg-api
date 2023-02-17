package org.waldreg.team.dto;

import java.util.List;

public class TeamRequestDto{

    private int teamBuildingId;
    private String teamName;

    private List<String> memberList;

    private TeamRequestDto(){}

    private TeamRequestDto(Builder builder){
        this.teamBuildingId = builder.teamBuildingId;
        this.teamName = builder.teamName;
        this.memberList = builder.memberList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getTeamBuildingId(){
        return teamBuildingId;
    }

    public String getTeamName(){
        return teamName;
    }

    public List<String> getMemberList(){
        return memberList;
    }

    public static final class Builder{

        private int teamBuildingId;
        private String teamName;
        private List<String> memberList;

        private Builder(){}

        public Builder teamBuildingId(int teamBuildingId){
            this.teamBuildingId = teamBuildingId;
            return this;
        }

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public Builder memberList(List<String> memberList){
            this.memberList = memberList;
            return this;
        }

        public TeamRequestDto build(){
            return new TeamRequestDto(this);
        }

    }

}
