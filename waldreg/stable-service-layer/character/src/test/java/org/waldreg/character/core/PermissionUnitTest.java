package org.waldreg.character.core;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PermissionUnitTest{

    @Test
    @DisplayName("String status PermissionUnit 생성 성공 테스트")
    public void CREATE_STRING_PERMISSION_UNIT_SUCCESS_TEST(){
        // given
        String permissionName = "String permission";
        List<String> statusList = List.of("success", "fail", "unknown");

        // when
        PermissionUnit<String> permissionUnit = PermissionUnit.<String>builder()
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

    @Test
    @DisplayName("Integer status PermissionUnit 생성 성공 테스트")
    public void CREATE_INTEGER_PERMISSION_UNIT_SUCCESS_TEST(){
        // given
        String permissionName = "Integer permission";
        List<Integer> statusList = List.of(0, 1);

        // when
        PermissionUnit<Integer> permissionUnit = PermissionUnit.<Integer>builder()
                .name(permissionName)
                .permissionVerifiable((i) -> (i == 0))
                .statusList(statusList)
                .build();

        // then
        Assertions.assertAll(
                () -> Assertions.assertTrue(permissionUnit.verify(0)),
                () -> Assertions.assertFalse(permissionUnit.verify(1))
        );
    }

}
