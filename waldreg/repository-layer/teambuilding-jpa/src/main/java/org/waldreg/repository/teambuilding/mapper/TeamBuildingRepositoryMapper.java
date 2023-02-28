package org.waldreg.repository.teambuilding.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.teambuilding.dto.TeamBuildingDto;

@Service
public class TeamBuildingRepositoryMapper{

    public TeamBuilding teamBuildingTitleToTeamBuildingDomain(String teamBuildingTitle){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingTitle)
                .build();
    }

    public List<TeamBuildingDto> teamBuildingListToTeamBuildingDtoList(List<TeamBuilding> teamBuildingList){
        List<TeamBuildingDto> teamBuildingDtoList = new ArrayList<>();
        for (TeamBuilding teamBuilding : teamBuildingList){
            teamBuildingDtoList.add(teamBuildingToTeamBuildingDto(teamBuilding));
        }
        return teamBuildingDtoList;
    }

    public TeamBuildingDto teamBuildingToTeamBuildingDto(TeamBuilding teamBuilding){
        TeamBuildingDto.Builder builder = TeamBuildingDto.builder()
                .teamBuildingId(teamBuilding.getTeamBuildingId())
                .teamBuildingTitle(teamBuilding.getTeamBuildingTitle())
                .lastModifiedAt(teamBuilding.getLastModifiedAt())
                .createdAt(teamBuilding.getCreatedAt());
        if (!isTeamListEmpty(teamBuilding)){
            return builder
                    .teamDtoList(teamListToTeamDtoList(teamBuilding.getTeamList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isTeamListEmpty(TeamBuilding teamBuilding){
        return teamBuilding.getTeamList() == null;
    }

    public List<TeamDto> teamListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamToTeamDto(team));
        }
        return teamDtoList;
    }

    public TeamDto teamToTeamDto(Team team){
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .teamBuildingId(team.getTeamBuilding().getTeamBuildingId())
                .teamName(team.getTeamName())
                .lastModifiedAt(team.getLastModifiedAt())
                .userDtoList(teamUserListToUserDtoList(team.getTeamUserList()))
                .build();
    }

    public List<UserDto> teamUserListToUserDtoList(List<TeamUser> teamUserList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (TeamUser teamUser : teamUserList){
            userDtoList.add(userToUserDto(teamUser.getUser()));
        }
        return userDtoList;
    }

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userId(user.getUserId())
                .build();
    }

}
