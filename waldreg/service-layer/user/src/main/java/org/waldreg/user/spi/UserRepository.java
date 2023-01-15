package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserRepository{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByName(String name);

    List<UserDto> readUserList(int stIdx, int enIdx);

    int readMaxIdx();

}
