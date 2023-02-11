package org.waldreg.token.aop;

import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.token.aop.annotation.BoardIdAuthenticating;
import org.waldreg.token.aop.annotation.HeaderPasswordAuthenticating;
import org.waldreg.token.aop.annotation.IdAuthenticating;
import org.waldreg.token.aop.annotation.UserIdAuthenticating;
import org.waldreg.token.aop.behavior.AuthFailBehavior;
import org.waldreg.token.aop.parameter.AuthenticateVerifyState;

public interface AuthenticateAopClient{

    @Authenticating
    void authenticate();

    @Authenticating(fail = AuthFailBehavior.PASS)
    boolean authenticateAndReturnParam(AuthenticateVerifyState state);

    @UserIdAuthenticating(idx = 1)
    void authenticateByUserId(int mockId, String userId);

    @UserIdAuthenticating(fail = AuthFailBehavior.PASS)
    boolean authenticateByUserIdAndReturnParam(int mockId, AuthenticateVerifyState state);

    @HeaderPasswordAuthenticating
    void authenticateByHeaderPassword();

    @HeaderPasswordAuthenticating(fail = AuthFailBehavior.PASS)
    boolean authenticateByHeaderPasswordAndReturnParam(AuthenticateVerifyState state);

    @IdAuthenticating
    void authenticateById(int id);

    @IdAuthenticating(fail = AuthFailBehavior.PASS)
    boolean authenticateByIdAndReturnParam(int id, AuthenticateVerifyState state);

    @BoardIdAuthenticating
    void authenticateByBoardId(int boardId);

    @BoardIdAuthenticating(fail = AuthFailBehavior.PASS)
    boolean authenticateByBoardIdAndReturnParam(int boardId, AuthenticateVerifyState state);

}
