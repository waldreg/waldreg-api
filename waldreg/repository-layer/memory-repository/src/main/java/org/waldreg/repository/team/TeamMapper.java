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

    @Override
    public List<Team> teamDtoListToTeamDomainList(List<TeamDto> teamDtoList){
        return null;
    }

    @Override
    public Team teamDtoToTeamDomain(TeamDto teamDto){
        return null;
    }

    @Override
    public List<User> userDtoListToUserDomainList(List<UserDto> userDtoList){
        return null;
    }

    @Override
    public User userDtoToUserDomain(UserDto userDto){
        return null;
    }

    @Override
    public List<TeamDto> teamDomainListToTeamDtoList(List<Team> teamList){
        return null;
    }

    @Override
    public TeamDto teamDomainToTeamDto(Team team){
        return null;
    }

    @Override
    public List<UserDto> userDomainListToUserDtoList(List<User> userList){
        return null;
    }

}
