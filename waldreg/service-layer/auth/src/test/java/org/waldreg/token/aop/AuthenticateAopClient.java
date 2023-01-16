package org.waldreg.token.aop;

import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.annotation.UserIdAuthenticating;

public interface AuthenticateAopClient{

    @Authenticating
    void authenticate();

    @UserIdAuthenticating(idx = 1)
    void authenticateByUserId(int mockId, String userId);

    @HeaderPasswordAuthenticating
    void authenticateByHeaderPassword();

}
