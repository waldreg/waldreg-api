package org.waldreg.domain.teambuilding;

import java.time.LocalDateTime;
import java.util.List;

public final class TeamBuilding{

    private int teamBuildingId;
    private String teamBuildingTitle;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;
    private List<Team> teamList;

    private TeamBuilding(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"TeamBuilding()\"");
    }

    private TeamBuilding(Builder builder){
        this.teamBuildingId = builder.teamBuildingId;
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.teamList = builder.teamList;
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

    public List<Team> getTeamList(){
        return teamList;
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

    public void setTeamList(List<Team> teamList){
        this.teamList = teamList;
    }

    public void addTeam(Team team){
        this.teamList.add(team);
    }

    public static final class Builder{

        private int teamBuildingId;
        private String teamBuildingTitle;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<Team> teamList;

        private Builder(){
            this.createdAt = LocalDateTime.now();
            this.lastModifiedAt = createdAt;
        }

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

        public Builder teamList(List<Team> teamList){
            this.teamList = teamList;
            return this;
        }

        public TeamBuilding build(){
            return new TeamBuilding(this);
        }

    }

}
