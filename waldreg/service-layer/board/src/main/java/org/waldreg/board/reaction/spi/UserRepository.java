package org.waldreg.board.reaction.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{

    UserDto getUserInfoByUserId(String userId);

}
