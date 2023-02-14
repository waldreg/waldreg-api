package org.waldreg.board.reaction.spi;

import org.waldreg.board.dto.UserDto;

public interface ReactionUserRepository{

    UserDto getUserInfoByUserId(String userId);

}
