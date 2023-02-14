package org.waldreg.user.spi;

import java.util.List;
import org.waldreg.user.dto.UserDto;

public interface JoiningPoolRepository{

    int readJoiningPoolMaxIdx();

    List<UserDto> readUserJoiningPool(int startIdx, int endIdx);

    void approveJoin(String userId);

    void rejectJoin(String userId);

    boolean isExistUserId(String userId);
}
