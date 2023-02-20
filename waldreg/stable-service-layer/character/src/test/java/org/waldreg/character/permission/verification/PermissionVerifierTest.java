package org.waldreg.character.permission.verification;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.extension.DefaultPermissionExtension;
import org.waldreg.character.permission.extension.PermissionExtension;
import org.waldreg.character.permission.management.PermissionUnitManager;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultPermissionVerifier.class, DefaultPermissionExtension.class, PermissionUnitManager.class})
class PermissionVerifierTest{

    @Autowired
    private PermissionVerifier permissionVerifier;

    @Autowired
    private PermissionExtension defaultPermissionExtension;

    @Test
    @DisplayName("PermissionVerifier 검증 동작 테스트")
    void VERIFY_PERMISSION_SUCCESS_TEST(){
        // given
        String name = "permission";
        List<String> statusList = List.of("success", "fail");

        PermissionUnit permissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name(name)
                .permissionVerifiable((s) -> s.equals("success"))
                .statusList(statusList)
                .build();

        // when
        defaultPermissionExtension.extend(permissionUnit);
        boolean resultSuccess = permissionVerifier.verify(name, statusList.get(0));
        boolean resultFail = permissionVerifier.verify(name, statusList.get(1));

        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(resultSuccess),
                () -> Assertions.assertFalse(resultFail)
        );
    }

}
