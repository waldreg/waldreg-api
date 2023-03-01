package org.waldreg.repository.teambuilding.team.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.teambuilding.TeamUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.teambuilding.mapper.TeamInTeamBuildingRepositoryMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

@Service
public class TeamRepositoryMapper implements TeamInTeamBuildingRepositoryMapper{

    public Team teamDtoToTeam(TeamDto teamDto){
        return Team.builder()
                .teamName(teamDto.getTeamName())
                .lastModifiedAt(teamDto.getLastModifiedAt())
                .build();
    }

    @Override
    public List<TeamDto> teamListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamToTeamDto(team));
        }
        return teamDtoList;
    }

    @Override
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

    public List<UserDto> userListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        userList.stream().forEach(u -> userDtoList.add(userToUserDto(u)));
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
