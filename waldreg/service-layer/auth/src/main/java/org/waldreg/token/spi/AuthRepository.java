package org.waldreg.token.spi;

import org.waldreg.token.aop.TokenUserFindable;

public interface AuthRepository extends TokenUserFindable, TokenUserFindUserIdAndPassword{
}
