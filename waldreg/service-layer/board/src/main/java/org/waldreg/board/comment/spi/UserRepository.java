package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{
    UserDto getUserInfo (int id);
}
