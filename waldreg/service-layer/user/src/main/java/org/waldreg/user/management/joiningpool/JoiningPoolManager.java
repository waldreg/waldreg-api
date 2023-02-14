package org.waldreg.user.management.joiningpool;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface JoiningPoolManager{

    void createUser(UserDto userDto);

    int readJoiningPoolMaxIdx();

    List<UserDto> readUserJoiningPool(int stIdx, int enIdx);

    void approveJoin(String userId);

    void rejectJoin(String userId);

}
