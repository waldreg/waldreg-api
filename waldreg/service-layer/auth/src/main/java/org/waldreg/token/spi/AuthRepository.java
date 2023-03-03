package org.waldreg.token.spi;

import org.waldreg.token.aop.TokenUserFindable;
import org.waldreg.token.dto.TokenUserDto;

public interface AuthRepository extends TokenUserFindable{

    boolean isExistUserId(String userId);

    TokenUserDto findUserByUserId(String userId);

}
