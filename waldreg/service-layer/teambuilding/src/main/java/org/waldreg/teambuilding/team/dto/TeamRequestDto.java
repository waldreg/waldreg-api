package org.waldreg.teambuilding.team.dto;

import java.util.List;

public class TeamRequestDto{

    private String teamName;
    private List<String> memberList;

    private TeamRequestDto(){}

    private TeamRequestDto(Builder builder){
        this.teamName = builder.teamName;
        this.memberList = builder.memberList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getTeamName(){
        return teamName;
    }

    public List<String> getMemberList(){
        return memberList;
    }

    public static final class Builder{

        private String teamName;
        private List<String> memberList;

        private Builder(){}

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
