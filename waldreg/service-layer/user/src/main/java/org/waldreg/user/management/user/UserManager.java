package org.waldreg.user.management.user;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserManager{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByUserId(String userId);

    List<UserDto> readUserList(int startIdx, int endIdx);

    void updateUser(int id, UserDto userDto);

    void updateCharacter(int id, String character);

    void deleteById(int id);

    int readMaxIdx();


}
