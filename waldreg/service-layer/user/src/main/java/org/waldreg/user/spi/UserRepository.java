package org.waldreg.user.spi;

import org.waldreg.user.dto.UserDto;

public interface UserRepository{

    void createUser(UserDto userDto);

}
