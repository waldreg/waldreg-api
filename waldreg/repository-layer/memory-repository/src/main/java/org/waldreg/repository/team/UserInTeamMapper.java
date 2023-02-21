package org.waldreg.repository.team;

import org.waldreg.domain.user.User;
import org.waldreg.teambuilding.dto.UserDto;

public interface UserInTeamMapper{

    UserDto userDomainToUserDto(User user);

}
