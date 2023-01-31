package org.waldreg.board.category.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{
    UserDto getUserInfo (int id);
}
