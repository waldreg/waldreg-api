package org.waldreg.token.spi;

import org.waldreg.token.aop.TokenUserFindById;

public interface AuthRepository extends TokenUserFindById, TokenUserFindUserIdAndPassword{
}
