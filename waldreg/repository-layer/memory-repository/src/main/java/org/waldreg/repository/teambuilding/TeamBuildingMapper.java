package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamBuilding;
import org.waldreg.domain.user.User;
import org.waldreg.teambuilding.dto.TeamBuildingDto;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

public class TeamBuildingMapper{

    public TeamBuilding teamBuildingTitleToTeamBuildingDomain(String teamBuildingTitle){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingTitle)
                .build();
    }

    public TeamBuilding teamBuildingDtoToTeamBuildingDomain(TeamBuildingDto teamBuildingDto){
        return TeamBuilding.builder()
                .teamBuildingTitle(teamBuildingDto.getTeamBuildingTitle())
                .teamBuildingId(teamBuildingDto.getTeamBuildingId())
                .teamList(teamDtoListToTeamDomainList(teamBuildingDto.getTeamList()))
                .lastModifiedAt(teamBuildingDto.getLastModifiedAt())
                .createdAt(teamBuildingDto.getCreatedAt())
                .build();
    }

    private boolean isUpdateTeamBuilding(TeamBuildingDto teamBuildingDto){
        return teamBuildingDto.getTeamList() != null;
    }

    private List<Team> teamDtoListToTeamDomainList(List<TeamDto> teamDtoList){
        List<Team> teamList = new ArrayList<>();
        for (TeamDto teamDto : teamDtoList){
            teamList.add(teamDtoToTeamDomain(teamDto));
        }
        return teamList;
    }

    private Team teamDtoToTeamDomain(TeamDto teamDto){
        return Team.builder()
                .teamId(teamDto.getTeamId())
                .teamBuildingId(teamDto.getTeamBuildingId())
                .teamName(teamDto.getTeamName())
                .lastModifiedAt(teamDto.getLastModifiedAt())
                .userList(userDtoListToUserDomainList(teamDto.getUserList()))
                .build();
    }

    private List<User> userDtoListToUserDomainList(List<UserDto> userDtoList){
        List<User> userList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userList.add(userDtoToUserDomain(userDto));
        }
        return userList;
    }

    private User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

    public TeamBuildingDto teamBuildingDomainToTeamBuildingDto(TeamBuilding teamBuilding){
        TeamBuildingDto.Builder builder = TeamBuildingDto.builder()
                .teamBuildingId(teamBuilding.getTeamBuildingId())
                .teamBuildingTitle(teamBuilding.getTeamBuildingTitle())
                .lastModifiedAt(teamBuilding.getLastModifiedAt())
                .createdAt(teamBuilding.getCreatedAt());
        if (!isTeamListEmpty(teamBuilding)){
            return builder
                    .teamDtoList(teamDomainListToTeamDtoList(teamBuilding.getTeamList()))
                    .build();
        }
        return builder.build();
    }

    private boolean isTeamListEmpty(TeamBuilding teamBuilding){
        return teamBuilding.getTeamList() == null;
    }

    private List<TeamDto> teamDomainListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamDomainToTeamDto(team));
        }
        return teamDtoList;
    }

    private TeamDto teamDomainToTeamDto(Team team){
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .teamBuildingId(team.getTeamBuildingId())
                .teamName(team.getTeamName())
                .lastModifiedAt(team.getLastModifiedAt())
                .userDtoList(userDomainListToUserDtoList(team.getUserList()))
                .build();
    }

    private List<UserDto> userDomainListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userDomainToUserDto(user));
        }
        return userDtoList;
    }

    private UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
