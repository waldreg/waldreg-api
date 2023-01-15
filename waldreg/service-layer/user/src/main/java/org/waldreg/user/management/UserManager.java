package org.waldreg.user.management;

import org.waldreg.user.dto.UserDto;

public interface UserManager{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

}
