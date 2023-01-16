package org.waldreg.token.aop;

import org.waldreg.token.dto.TokenUserDto;

public interface TokenUserFindById{

    TokenUserDto findUserById(int id);

}