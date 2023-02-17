package org.waldreg.team.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.waldreg.teambuilding.dto.UserDto;

public class TeamDto{

    private final int teamId;
    private final int teamBuildingId;
    private String teamName;
    private final LocalDateTime lastModifiedAt;
    private List<UserDto> userDtoList;

    private TeamDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"TeamDto()\"");
    }

    private TeamDto(Builder builder){
        this.teamId = builder.teamId;
        this.teamBuildingId = builder.teamBuildingId;
        this.teamName = builder.teamName;
        this.lastModifiedAt = builder.lastModifiedAt;
        this.userDtoList = builder.userDtoList;
    }

    public static Builder builder(){
        return new Builder();
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

    public List<UserDto> getUserList(){
        return userDtoList;
    }

    public int getTeamBuildingId(){
        return teamBuildingId;
    }

    public void setTeamName(String teamName){
        this.teamName = teamName;
    }

    public void setUserDtoList(List<UserDto> userDtoList){
        this.userDtoList = userDtoList;
    }

    public static final class Builder{

        private int teamId;
        private int teamBuildingId;
        private String teamName;
        private LocalDateTime lastModifiedAt;
        private List<UserDto> userDtoList;

        private Builder(){}

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

        public Builder userDtoList(List<UserDto> userDtoList){
            this.userDtoList = userDtoList;
            return this;
        }

        public Builder teamBuildingId(int teamBuildingId){
            this.teamBuildingId = teamBuildingId;
            return this;
        }

        public TeamDto build(){
            return new TeamDto(this);
        }

    }

}
