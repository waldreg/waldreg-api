package org.waldreg.character.extension;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.character.core.PermissionUnit;
import org.waldreg.character.exception.DuplicatedPermissionNameException;
import org.waldreg.character.management.PermissionUnitManager;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PermissionExtension.class, PermissionUnitManager.class})
public class PermissionExtensionTest{

    @Autowired
    private PermissionExtension permissionExtension;

    @Autowired
    private PermissionUnitManager permissionUnitManager;

    @BeforeEach
    @AfterEach
    public void deleteAllPermissions(){
        permissionUnitManager.deleteAllPermission();
    }

    @Test
    @DisplayName("새로운 역할 확장 테스트")
    public void EXTEND_NEW_PERMISSION_TEST(){
        // given
        PermissionUnit permissionUnit = PermissionUnit.builder()
                .name("new permission")
                .permissionVerifiable((s) -> s.equals("success"))
                .statusList(List.of("success", "fail", "something..."))
                .build();

        // when & then
        Assertions.assertDoesNotThrow(() -> permissionExtension.extend(permissionUnit));
    }

    @Test
    @DisplayName("새로운 역할 확장 실패 테스트 - 중복 이름")
    public void EXTEND_NEW_PERMISSION_FAIL_DUPLICATED_NAME_TEST(){
        // given
        PermissionUnit permissionUnit = PermissionUnit.builder()
                .name("duplicated name")
                .permissionVerifiable((s) -> s.equals("success"))
                .statusList(List.of("success", "fail", "something..."))
                .build();

        // when & then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> permissionExtension.extend(permissionUnit)),
                () -> Assertions.assertThrows(DuplicatedPermissionNameException.class, () -> permissionExtension.extend(permissionUnit))
        );
    }

}
