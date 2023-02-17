package org.waldreg.team.spi;

import org.waldreg.team.dto.UserDto;

public interface TeamUserRepository{

    UserDto getUserInfoByUserId(String userId);

    boolean isExistUserByUserId(String userId);

}
