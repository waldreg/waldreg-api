package org.waldreg.character.permission.core;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PermissionUnitTest{

    @Test
    @DisplayName("String status PermissionUnit 생성 성공 테스트")
    void CREATE_STRING_PERMISSION_UNIT_SUCCESS_TEST(){
        // given
        String permissionName = "String permission";
        List<String> statusList = List.of("success", "fail", "unknown");

        // when
        PermissionUnit permissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name(permissionName)
                .permissionVerifiable((s) -> s.equals("success"))
                .statusList(statusList)
                .build();

        // then
        Assertions.assertAll(
                ()-> Assertions.assertTrue(permissionUnit.verify("success")),
                ()-> Assertions.assertFalse(permissionUnit.verify("fail")),
                ()-> Assertions.assertFalse(permissionUnit.verify("unknown"))
        );
    }

}
