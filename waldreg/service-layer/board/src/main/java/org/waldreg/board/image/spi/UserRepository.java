package org.waldreg.board.image.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{
    UserDto getUserInfo (int id);
}
