package org.waldreg.repository.team;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.TeamInTeamBuildingMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

public class TeamMapper implements TeamInTeamBuildingMapper{

    public Team teamDtoToTeamDomain(TeamDto teamDto){
        return Team.builder()
                .teamId(teamDto.getTeamId())
                .teamBuildingId(teamDto.getTeamBuildingId())
                .teamName(teamDto.getTeamName())
                .lastModifiedAt(teamDto.getLastModifiedAt())
                .userList(userDtoListToUserDomainList(teamDto.getUserList()))
                .build();
    }

    public List<User> userDtoListToUserDomainList(List<UserDto> userDtoList){
        List<User> userList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userList.add(userDtoToUserDomain(userDto));
        }
        return userList;
    }

    public User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

    public List<TeamDto> teamDomainListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamDomainToTeamDto(team));
        }
        return teamDtoList;
    }

    public TeamDto teamDomainToTeamDto(Team team){
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .teamBuildingId(team.getTeamBuildingId())
                .teamName(team.getTeamName())
                .lastModifiedAt(team.getLastModifiedAt())
                .userDtoList(userDomainListToUserDtoList(team.getUserList()))
                .build();
    }

    public List<UserDto> userDomainListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userDomainToUserDto(user));
        }
        return userDtoList;
    }

    public UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

}
