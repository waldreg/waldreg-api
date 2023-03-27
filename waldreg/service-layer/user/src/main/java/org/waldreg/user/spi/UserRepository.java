package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserRepository{

    void createUser(UserDto userDto);

    UserDto readUserById(int id);

    UserDto readUserByUserId(String userId);

    List<UserDto> readUserList(int startIdx, int endIdx);

    void updateUser(int id, UserDto userDto);

    void updateCharacter(int id, String character);

    int readMaxIdx();

    void deleteById(int id);

    boolean isExistUserId(String userId);

    boolean isExistId(int id);

    List<UserDto> readSpecificUserList(List<Integer> idList);

}
