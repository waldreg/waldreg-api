package org.waldreg.character.permission.management;

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
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.exception.DuplicatedPermissionNameException;
import org.waldreg.character.exception.UnknownPermissionException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PermissionUnitManager.class})
class PermissionUnitManagerTest{

    @Autowired
    private PermissionUnitManager permissionUnitManager;

    @BeforeEach
    @AfterEach
    public void deleteAllPermissions(){
        permissionUnitManager.deleteAllPermission();
    }

    @Test
    @DisplayName("PermissionUnit 생성 성공 테스트")
    void ADD_DIFFERENT_TYPE_PERMISSION_UNIT_TEST(){
        // given
        PermissionUnit stringPermissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name("permission 1")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        PermissionUnit integerPermissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name("permission 2")
                .permissionVerifiable((s) -> s.equals("false"))
                .statusList(List.of("true", "false"))
                .build();

        // when & then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> permissionUnitManager.add(stringPermissionUnit)),
                () -> Assertions.assertDoesNotThrow(() -> permissionUnitManager.add(integerPermissionUnit))
        );
    }

    @Test
    @DisplayName("중복 이름 PermissionUnit 등록 실패 테스트")
    void ADD_DUPLICATED_NAME_PERMISSION_UNIT_TEST(){
        // given
        PermissionUnit stringPermissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name("String permission")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        // when & then
        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> permissionUnitManager.add(stringPermissionUnit)),
                () -> Assertions.assertThrows(DuplicatedPermissionNameException.class,
                        () -> permissionUnitManager.add(stringPermissionUnit))
        );
    }

    @Test
    @DisplayName("PermissionUnit 이름 조회 성공 테스트")
    void GET_PERMISSION_UNIT_BY_NAME_TEST(){
        // given
        String permissionName = "name";
        PermissionUnit stringPermissionUnit = DefaultPermissionUnit.builder()
                .service("character")
                .name(permissionName)
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        // when
        permissionUnitManager.add(stringPermissionUnit);
        PermissionUnit result = permissionUnitManager.getPermission(permissionName);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(permissionName, result.getName()),
                () -> Assertions.assertTrue(result.verify("true")),
                () -> Assertions.assertFalse(result.verify("false"))
        );
    }

    @Test
    @DisplayName("PermissionUnit 이름 조회 실패 테스트 - Permission을 찾을 수 없음")
    void GET_PERMISSION_UNIT_BY_NAME_FAIL_UNKNOWN_PERMISSION_TEST(){
        // given
        String permissionName = "unknown name";

        // when && then
        Assertions.assertThrows(UnknownPermissionException.class, () -> permissionUnitManager.getPermission(permissionName));
    }

    @Test
    @DisplayName("PermissionUnitList 조회 성공 테스트")
    void GET_PERMISSION_UNIT_LIST_TEST(){
        // given
        PermissionUnit permissionUnit1 = DefaultPermissionUnit.builder()
                .service("character")
                .name("permission 1")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        PermissionUnit permissionUnit2 = DefaultPermissionUnit.builder()
                .service("character")
                .name("permission 2")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        PermissionUnit permissionUnit3 = DefaultPermissionUnit.builder()
                .service("character")
                .name("permission 3")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();

        // when
        permissionUnitManager.add(permissionUnit1);
        permissionUnitManager.add(permissionUnit2);
        permissionUnitManager.add(permissionUnit3);
        List<PermissionUnit> result = permissionUnitManager.getPermissionUnitList();

        // then
        Assertions.assertEquals(3, result.size());
    }

}
