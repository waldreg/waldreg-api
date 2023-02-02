package org.waldreg.board.board.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{

    UserDto getUserInfo(int id);

    String getUserTier(int id);

    Boolean isExistUser(int authorId);

}
