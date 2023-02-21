package org.waldreg.teambuilding.teambuilding.spi;

import org.waldreg.teambuilding.dto.UserDto;

public interface TeamBuildingUserRepository{

    UserDto getUserInfoByUserId(String userId);

    boolean isExistUserByUserId(String userId);

}
