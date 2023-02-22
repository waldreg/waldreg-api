package org.waldreg.character.management;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.exception.UnknownPermissionStatusException;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitManager;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.character.spi.UserExistChecker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultCharacterManager.class, PermissionUnitManager.class})
class DefaultCharacterManagerTest{

    @Autowired
    private CharacterManager defaultCharacterManager;

    @Autowired
    private PermissionUnitManager permissionUnitManager;

    @MockBean
    private CharacterRepository characterRepository;

    @MockBean
    private UserExistChecker userExistChecker;

    private final String permissionName = "mock permission";

    @BeforeEach
    @AfterEach
    void initPermission(){
        permissionUnitManager.deleteAllPermission();
        PermissionUnit permissionUnit = DefaultPermissionUnit.builder()
                .name(permissionName)
                .statusList(List.of("true", "fail"))
                .build();
        permissionUnitManager.add(permissionUnit);
    }


    @Test
    @DisplayName("새로운 Character 추가 성공 테스트")
    void CREATE_NEW_CHARACTER_SUCCESS_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("character")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("true")
                                .build()
                )).build();

        // when & then
        Assertions.assertDoesNotThrow(() -> defaultCharacterManager.createCharacter(character));
    }

    @Test
    @DisplayName("새로운 Character 추가 실패 테스트 - permission name 에 해당하는 permission 을 찾을 수 없음")
    void CREATE_NEW_CHARACTER_FAIL_DOES_NOT_FIND_PERMISSION_TEST(){
        // given
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(12)
                                .name("Unknown Character")
                                .status("fail")
                                .build()
                )).build();

        // when & then
        Assertions.assertThrows(UnknownPermissionException.class, () -> defaultCharacterManager.createCharacter(character));
    }

    @Test
    @DisplayName("새로운 Character 추가 실패 테스트 - permission name 이 갖고있는 permission status 가 아님")
    void CREATE_NEW_CHARACTER_FAIL_INVALID_STATUS_NAME_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        CharacterDto character = CharacterDto.builder()
                .characterName("new character")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("invalid")
                                .build()
                )).build();

        // when & then
        Assertions.assertThrows(UnknownPermissionStatusException.class, () -> defaultCharacterManager.createCharacter(character));
    }

    @Test
    @DisplayName("특정 Character 조회 성공 테스트")
    void READ_CHARACTER_SUCCESS_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        String characterName = "characterName";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(characterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(characterDto);
        Mockito.when(characterRepository.readCharacter(characterName)).thenReturn(Optional.of(characterDto));
        CharacterDto result = defaultCharacterManager.readCharacter(characterName);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(characterDto.getCharacterName(), result.getCharacterName()),
                () -> Assertions.assertEquals(characterDto.getPermissionList().size(), result.getPermissionList().size())
        );
    }

    @Test
    @DisplayName("유저 id에 속한 Character 조회 테스트")
    void READ_CHARACTER_BY_USER_ID_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        int id = 1;
        String characterName = "admin";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(characterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        Mockito.when(userExistChecker.isExistUser(Mockito.anyInt())).thenReturn(true);
        Mockito.when(defaultCharacterManager.readCharacterByUserId(id)).thenReturn(characterDto);
        CharacterDto result = defaultCharacterManager.readCharacterByUserId(id);

        // then
        Assertions.assertEquals("admin", characterDto.getCharacterName());
    }

    @Test
    @DisplayName("Character 목록 조회 성공 테스트")
    void READ_CHARACTER_LIST_SUCCESS_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        CharacterDto characterDto1 = CharacterDto.builder()
                .characterName("1")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        CharacterDto characterDto2 = CharacterDto.builder()
                .characterName("2")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(characterDto1);
        defaultCharacterManager.createCharacter(characterDto2);
        Mockito.when(defaultCharacterManager.readCharacterList()).thenReturn(List.of(characterDto1, characterDto2));
        List<CharacterDto> result = defaultCharacterManager.readCharacterList();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(characterDto1.getCharacterName(), result.get(0).getCharacterName()),
                () -> Assertions.assertEquals(characterDto2.getCharacterName(), result.get(1).getCharacterName())
        );
    }

    @Test
    @DisplayName("Character 수정 성공 테스트")
    void UPDATE_CHARACTER_SUCCESS_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(beforeCharacter);
        Mockito.when(characterRepository.readCharacter(afterName)).thenReturn(Optional.empty());
        Mockito.when(characterRepository.readCharacter(beforeName)).thenReturn(Optional.of(beforeCharacter));
        defaultCharacterManager.updateCharacter(beforeName, afterCharacter);
        Mockito.when(characterRepository.readCharacter(afterName)).thenReturn(Optional.of(afterCharacter));
        CharacterDto result = defaultCharacterManager.readCharacter(afterName);

        // then
        Assertions.assertEquals(afterName, result.getCharacterName());
    }

    @Test
    @DisplayName("Character 수정 실패 테스트 - 존재하지 않는 권한을 수정하려고 할 경우")
    void UPDATE_CHARACTER_FAIL_UNKNOWN_PERMISSION_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(2)
                                .name("unknown permission")
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(beforeCharacter);
        Mockito.when(characterRepository.readCharacter(beforeName)).thenReturn(Optional.of(beforeCharacter));
        Mockito.when(characterRepository.readCharacter(afterName)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(UnknownPermissionException.class, () -> defaultCharacterManager.updateCharacter(beforeName, afterCharacter));
    }

    @Test
    @DisplayName("Character 수정 실패 테스트 - 해당 권한에 해당하지 않는 permission status 로 변경하려고 시도하는 경우")
    void UPDATE_CHARACTER_FAIL_UNKNOWN_PERMISSION_STATUS_TEST(){
        // given
        PermissionUnit permissionUnit = permissionUnitManager.getPermission("mock permission");
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("service")
                                .id(permissionUnit.getId())
                                .name(permissionName)
                                .status("never used")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(beforeCharacter);
        Mockito.when(characterRepository.readCharacter(beforeName)).thenReturn(Optional.of(beforeCharacter));
        Mockito.when(characterRepository.readCharacter(afterName)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(UnknownPermissionStatusException.class, () -> defaultCharacterManager.updateCharacter(beforeName, afterCharacter));
    }

    @Test
    @DisplayName("Character 삭제 성공 테스트")
    void DELETE_CHARACTER_SUCCESS_TEST(){
        // given
        String characterName = "characterName";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(characterName)
                .permissionDtoList(List.of()).build();

        // when
        Mockito.when(characterRepository.readCharacter(characterName)).thenReturn(Optional.of(characterDto));

        // then
        Assertions.assertDoesNotThrow(() -> defaultCharacterManager.deleteCharacter(characterName));
    }

}
