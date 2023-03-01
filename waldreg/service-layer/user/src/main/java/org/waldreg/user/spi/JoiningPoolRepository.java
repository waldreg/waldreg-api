package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface JoiningPoolRepository{

    void createUser(UserDto userDto);

    int readJoiningPoolMaxIdx();

    List<UserDto> readUserJoiningPool(int startIdx, int endIdx);

    UserDto getUserByUserId(String userId);

    void deleteUserByUserId(String userId);

    boolean isExistUserId(String userId);
}
