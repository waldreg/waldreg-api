package org.waldreg.domain.teambuilding;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TEAM")
public final class Team{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TEAM_TEAM_ID")
    private Integer teamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_BUILDING_TEAM_BUILDING_ID")
    private TeamBuilding teamBuilding;

    @Column(name = "TEAM_TEAM_NAME", length = 1005, nullable = false)
    private String teamName;

    @Column(name = "TEAM_LAST_MODIFIED_AT", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedAt;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TeamUser> teamUserList;

    private Team(){}

    private Team(Builder builder){
        this.teamBuilding = builder.teamBuilding;
        this.teamName = builder.teamName;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.teamUserList = builder.teamUserList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getTeamId(){
        return teamId;
    }

    public String getTeamName(){
        return teamName;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public List<TeamUser> getTeamUserList(){
        return teamUserList;
    }

    public TeamBuilding getTeamBuilding(){
        return teamBuilding;
    }

    public void setTeamName(String teamName){
        this.teamName = teamName;
    }

    public void setLastModifiedAt(LocalDateTime lastModifiedAt){
        this.lastModifiedAt = lastModifiedAt;
    }

    public void setTeamUserList(List<TeamUser> teamUserList){
        this.teamUserList = teamUserList;
    }

    public static final class Builder{

        private TeamBuilding teamBuilding;
        private String teamName;
        private LocalDateTime lastModifiedAt;
        private List<TeamUser> teamUserList;

        private Builder(){
            lastModifiedAt = LocalDateTime.now();
        }

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder userList(List<TeamUser> teamUserList){
            this.teamUserList = teamUserList;
            return this;
        }

        public Builder teamBuilding(TeamBuilding teamBuilding){
            this.teamBuilding = teamBuilding;
            return this;
        }

        public Team build(){
            return new Team(this);
        }

    }

}
