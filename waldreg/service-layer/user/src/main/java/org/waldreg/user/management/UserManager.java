package org.waldreg.user.management;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserManager{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByName(String name);

    List<UserDto> readUserList(int stIdx, int enIdx);

    void updateUser(int idx, UserDto userDto);

}
