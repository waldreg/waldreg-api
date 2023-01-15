package org.waldreg.character.management;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.exception.UnknownPermissionStatusException;
import org.waldreg.character.management.CharacterManager;
import org.waldreg.character.management.DefaultCharacterManager;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitManager;
import org.waldreg.character.spi.CharacterRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultCharacterManager.class, PermissionUnitManager.class})
public class DefaultCharacterManagerTest{

    @Autowired
    private CharacterManager defaultCharacterManager;

    @Autowired
    private PermissionUnitManager permissionUnitManager;

    @MockBean
    private CharacterRepository characterRepository;

    private final String permissionName = "mock permission";

    @BeforeEach
    @AfterEach
    public void initPermission(){
        permissionUnitManager.deleteAllPermission();
        PermissionUnit permissionUnit = DefaultPermissionUnit.builder()
                .name(permissionName)
                .statusList(List.of("true", "fail"))
                .build();
        permissionUnitManager.add(permissionUnit);
    }


    @Test
    @DisplayName("새로운 Character 추가 성공 테스트")
    public void CREATE_NEW_CHARACTER_SUCCESS_TEST(){
        // given
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("true")
                                .build()
                )).build();

        // when & then
        Assertions.assertDoesNotThrow(() -> defaultCharacterManager.createCharacter(character));
    }

    @Test
    @DisplayName("새로운 Character 추가 실패 테스트 - permission name 에 해당하는 permission 을 찾을 수 없음")
    public void CREATE_NEW_CHARACTER_FAIL_DOES_NOT_FIND_PERMISSION_TEST(){
        // given
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name("Unknown Character")
                                .status("fail")
                                .build()
                )).build();

        // when & then
        Assertions.assertThrows(UnknownPermissionException.class, () -> defaultCharacterManager.createCharacter(character));
    }

    @Test
    @DisplayName("새로운 Character 추가 실패 테스트 - permission name 이 갖고있는 permission status 가 아님")
    public void CREATE_NEW_CHARACTER_FAIL_INVALID_STATUS_NAME_TEST(){
        // given
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("invalid")
                                .build()
                )).build();

        // when & then
        Assertions.assertThrows(UnknownPermissionStatusException.class, () -> defaultCharacterManager.createCharacter(character));
    }

}
