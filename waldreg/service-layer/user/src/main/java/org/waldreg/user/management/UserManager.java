package org.waldreg.user.management;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserManager{

    void createUser(UserDto userDto);

    int readJoiningPoolMaxIdx();

    List<UserDto> readUserJoiningPool(int stIdx, int enIdx);

    void approveJoin(String userId);

    void rejectJoin(String userId);

    UserDto readUserById(int id);

    UserDto readUserByUserId(String userId);

    List<UserDto> readUserList(int startIdx, int endIdx);

    void updateUser(int id, UserDto userDto);

    void updateCharacter(int id, String character);

    void deleteById(int id);

    int readMaxIdx();


}
