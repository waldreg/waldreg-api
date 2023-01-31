package org.waldreg.board.file.spi;

import org.waldreg.board.dto.UserDto;

public interface UserRepository{
    UserDto getUserInfo (int id);
}
