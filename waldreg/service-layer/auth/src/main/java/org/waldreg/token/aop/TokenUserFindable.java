package org.waldreg.token.aop;

import org.waldreg.token.dto.TokenUserDto;

public interface TokenUserFindable{

    TokenUserDto findUserById(int id);

    TokenUserDto findUserByBoardId(int boardId);

}