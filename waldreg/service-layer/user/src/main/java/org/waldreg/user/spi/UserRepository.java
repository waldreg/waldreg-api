package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface UserRepository{

    void createUser(UserDto userDto);

    int readJoiningPoolMaxIdx();

    List<UserDto> readUserJoiningPool(int startIdx, int endIdx);

    void approveJoin(String userId);

    void rejectJoin(String userId);

    UserDto readUserById(int id);

    UserDto readUserByUserId(String userId);

    List<UserDto> readUserList(int startIdx, int endIdx);

    void updateUser(int idx, UserDto userDto);

    void updateCharacter(int id, String character);

    int readMaxIdx();

    void deleteById(int id);

}
