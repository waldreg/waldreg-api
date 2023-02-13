package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.UserDto;

public interface CommentUserRepository{

    UserDto getUserInfo(int id);

}
