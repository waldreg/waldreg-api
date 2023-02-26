package org.waldreg.repository.board;

import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.user.User;

public interface UserInBoardMapper{

    UserDto userDomainToUserDto(User user);

}
