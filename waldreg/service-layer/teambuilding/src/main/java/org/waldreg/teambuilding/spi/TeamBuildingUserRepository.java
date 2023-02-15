package org.waldreg.teambuilding.spi;

import org.waldreg.teambuilding.dto.UserDto;

public interface TeamBuildingUserRepository{

    UserDto getUserInfoByUserId(String userId);

}
