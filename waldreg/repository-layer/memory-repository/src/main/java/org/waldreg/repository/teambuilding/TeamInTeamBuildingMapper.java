package org.waldreg.repository.teambuilding;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.domain.teambuilding.Team;
import org.waldreg.domain.user.User;
import org.waldreg.teambuilding.dto.TeamDto;
import org.waldreg.teambuilding.dto.UserDto;

public interface TeamInTeamBuildingMapper{

    Team teamDtoToTeamDomain(TeamDto teamDto);

    List<User> userDtoListToUserDomainList(List<UserDto> userDtoList);

    User userDtoToUserDomain(UserDto userDto);

    TeamDto teamDomainToTeamDto(Team team);

    List<UserDto> userDomainListToUserDtoList(List<User> userList);

    UserDto userDomainToUserDto(User user);

}
