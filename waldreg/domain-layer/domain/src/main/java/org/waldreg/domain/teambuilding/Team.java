package org.waldreg.domain.teambuilding;

import java.time.LocalDateTime;
import java.util.List;
import org.waldreg.domain.user.User;

public final class Team{

    private int teamId;
    private int teamBuildingId;
    private String teamName;
    private LocalDateTime lastModifiedAt;
    private List<User> userList;

    private Team(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"Team()\"");
    }

    private Team(Builder builder){
        this.teamId = builder.teamId;
        this.teamBuildingId = builder.teamBuildingId;
        this.teamName = builder.teamName;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.userList = builder.userList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setTeamId(int teamId){
        this.teamId = teamId;
    }

    public int getTeamId(){
        return teamId;
    }

    public String getTeamName(){
        return teamName;
    }

    public LocalDateTime getLastModifiedAt(){
        return lastModifiedAt;
    }

    public List<User> getUserList(){
        return userList;
    }

    public int getTeamBuildingId(){
        return teamBuildingId;
    }

    public static final class Builder{

        private int teamId;
        private int teamBuildingId;
        private String teamName;
        private LocalDateTime lastModifiedAt;
        private List<User> userList;

        private Builder(){
            lastModifiedAt = LocalDateTime.now();
        }

        public Builder teamId(int teamId){
            this.teamId = teamId;
            return this;
        }

        public Builder teamName(String teamName){
            this.teamName = teamName;
            return this;
        }

        public Builder lastModifiedAt(LocalDateTime lastModifiedAt){
            this.lastModifiedAt = lastModifiedAt;
            return this;
        }

        public Builder userList(List<User> userList){
            this.userList = userList;
            return this;
        }

        public Builder teamBuildingId(int teamBuildingId){
            this.teamBuildingId = teamBuildingId;
            return this;
        }

        public Team build(){
            return new Team(this);
        }

    }

}
