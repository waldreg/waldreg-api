package org.waldreg.domain.teambuilding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TEAM_BUILDING")
public final class TeamBuilding{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_BUILDING_TEAM_BUILDING_ID")
    private Integer teamBuildingId;

    @Column(name = "TEAM_BUILDING_TEAM_BUILDING_TITLE", nullable = false, length = 1000)
    private String teamBuildingTitle;

    @Column(name = "TEAM_BUILDING_CREATED_AT", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "TEAM_BUILDING_LAST_MODIFIED_AT", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedAt;

    @OneToMany(mappedBy = "teamBuilding", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Team> teamList = new ArrayList<>();

    private TeamBuilding(){}

    private TeamBuilding(Builder builder){
        this.teamBuildingTitle = builder.teamBuildingTitle;
        this.createdAt = builder.createdAt;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.teamList = builder.teamList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getTeamBuildingId(){
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
        teamList.clear();
        this.teamList.addAll(teamList);
    }

    public void addTeam(Team team){
        this.teamList.add(team);
    }

    public void deleteTeam(int teamId){
        teamList.stream().filter(t -> t.getTeamId() == teamId).findFirst().ifPresentOrElse(
                t -> {
                    t.setTeamBuilding(null);
                    teamList.remove(t);
                },
                () -> {throw new IllegalStateException("Cannot find team with id \"" + teamId + "\"");}
        );

    }

    public static final class Builder{

        private String teamBuildingTitle;
        private LocalDateTime createdAt;
        private LocalDateTime lastModifiedAt;
        private List<Team> teamList = new ArrayList<>();

        private Builder(){
            this.createdAt = LocalDateTime.now();
            this.lastModifiedAt = createdAt;
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
