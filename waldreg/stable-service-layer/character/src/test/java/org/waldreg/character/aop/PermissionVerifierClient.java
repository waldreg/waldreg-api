package org.waldreg.character.aop;

import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.aop.behavior.VerifyingFailBehavior;
import org.waldreg.character.aop.parameter.PermissionVerifyState;

public interface PermissionVerifierClient{
    @PermissionVerifying("Permission verifier aop test")
    void method();

    @PermissionVerifying("Permission verifier aop test - unknown permission")
    void unknownPermission();

    @PermissionVerifying("Permission verifier aop test")
    PermissionVerifyState state(String string, PermissionVerifyState permissionVerifyState);

    @PermissionVerifying(value = "Permission verifier aop test", fail = VerifyingFailBehavior.PASS)
    PermissionVerifyState failState(int idx, PermissionVerifyState permissionVerifyState);
}
