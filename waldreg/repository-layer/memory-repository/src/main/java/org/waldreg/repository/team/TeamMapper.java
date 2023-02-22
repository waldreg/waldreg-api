package org.waldreg.repository.team;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.TeamInTeamBuildingMapper;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

@Service
public class TeamMapper implements TeamInTeamBuildingMapper{

    private final UserInTeamMapper userInTeamMapper;

    public TeamMapper(UserInTeamMapper userInTeamMapper){this.userInTeamMapper = userInTeamMapper;}

    @Override
    public List<Team> teamDtoListToTeamDomainList(List<TeamDto> teamDtoList){
        List<Team> teamList = new ArrayList<>();
        for (TeamDto teamDto : teamDtoList){
            teamList.add(teamDtoToTeamDomain(teamDto));
        }
        return teamList;
    }

    @Override
    public Team teamDtoToTeamDomain(TeamDto teamDto){
        return Team.builder()
                .teamId(teamDto.getTeamId())
                .teamBuildingId(teamDto.getTeamBuildingId())
                .teamName(teamDto.getTeamName())
                .lastModifiedAt(teamDto.getLastModifiedAt())
                .userList(userDtoListToUserDomainList(teamDto.getUserList()))
                .build();
    }

    @Override
    public List<User> userDtoListToUserDomainList(List<UserDto> userDtoList){
        List<User> userList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userList.add(userDtoToUserDomain(userDto));
        }
        return userList;
    }

    @Override
    public User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

    @Override
    public List<TeamDto> teamDomainListToTeamDtoList(List<Team> teamList){
        List<TeamDto> teamDtoList = new ArrayList<>();
        for (Team team : teamList){
            teamDtoList.add(teamDomainToTeamDto(team));
        }
        return teamDtoList;
    }

    @Override
    public TeamDto teamDomainToTeamDto(Team team){
        return TeamDto.builder()
                .teamId(team.getTeamId())
                .teamBuildingId(team.getTeamBuildingId())
                .teamName(team.getTeamName())
                .lastModifiedAt(team.getLastModifiedAt())
                .userDtoList(userDomainListToUserDtoList(team.getUserList()))
                .build();
    }

    @Override
    public List<UserDto> userDomainListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userInTeamMapper.userDomainToUserDto(user));
        }
        return userDtoList;
    }

}
