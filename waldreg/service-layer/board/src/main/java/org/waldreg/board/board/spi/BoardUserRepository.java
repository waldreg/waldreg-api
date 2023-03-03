package org.waldreg.board.board.spi;

import org.waldreg.board.dto.UserDto;

public interface BoardUserRepository{

    UserDto getUserInfo(int id);

}
