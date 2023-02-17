package org.waldreg.teambuilding.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TeamBuildingDto{

    private int teamBuildingId;
    private String teamBuildingTitle;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private List<TeamDto> teamDtoList;

    private TeamBuildingDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"TeamBuilding()\"");
    }

    private TeamBuildingDto(Builder builder){
        this.teamBuildingId = builder.teamBuildingId;
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.teamDtoList = builder.teamDtoList;
    }

    public static Builder builder(){
        return new Builder();
    }

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

    public List<TeamDto> getTeamList(){
        return teamDtoList;
    }

    public void setTeamBuildingId(int teamBuildingId){
        this.teamBuildingId = teamBuildingId;
    }

    public void setTeamBuildingTitle(String teamBuildingTitle){
        this.teamBuildingTitle = teamBuildingTitle;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public void setTeamDtoList(List<TeamDto> teamDtoList){
        this.teamDtoList = teamDtoList;
    }

    public static final class Builder{

        private int teamBuildingId;
        private String teamBuildingTitle;
        private final LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<TeamDto> teamDtoList;

        private Builder(){
            this.createdAt = LocalDateTime.now();
        }

        public Builder teamBuildingId(int teamBuildingId){
            this.teamBuildingId = teamBuildingId;
            return this;
        }

        public Builder teamBuildingTitle(String teamBuildingTitle){
            this.teamBuildingTitle = teamBuildingTitle;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder teamDtoList(List<TeamDto> teamDtoList){
            this.teamDtoList = teamDtoList;
            return this;
        }

        public TeamBuildingDto build(){
            return new TeamBuildingDto(this);
        }

    }

}
