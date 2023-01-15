package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserRepository{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByName(String name);

    List<UserDto> readUserList(int startIdx, int endIdx);

    void updateUser(int idx, UserDto userDto);

    int readMaxIdx();

}
