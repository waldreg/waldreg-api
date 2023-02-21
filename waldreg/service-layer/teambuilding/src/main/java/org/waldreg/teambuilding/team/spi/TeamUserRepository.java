package org.waldreg.teambuilding.team.spi;

import org.waldreg.teambuilding.dto.UserDto;

public interface TeamUserRepository{

    UserDto getUserInfoByUserId(String userId);

    boolean isExistUserByUserId(String userId);

}
