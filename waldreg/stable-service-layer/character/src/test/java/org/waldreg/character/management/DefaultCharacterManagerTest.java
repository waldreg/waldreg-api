package org.waldreg.character.management;

import java.util.List;
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

    @Test
    @DisplayName("특정 Character 조회 성공 테스트")
    public void READ_CHARACTER_SUCCESS_TEST(){
        // given
        String characterName = "characterName";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(characterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(characterDto);
        Mockito.when(characterRepository.readCharacter(characterName)).thenReturn(characterDto);
        CharacterDto result = defaultCharacterManager.readCharacter(characterName);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(characterDto.getCharacterName(), result.getCharacterName()),
                () -> Assertions.assertEquals(characterDto.getPermissionList().size(), result.getPermissionList().size())
        );
    }

    @Test
    @DisplayName("유저 id에 속한 Character 조회 테스트")
    public void READ_CHARACTER_BY_USER_ID_TEST(){
        // given
        int id = 1;
        String characterName = "admin";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(characterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        Mockito.when(defaultCharacterManager.readCharacterByUserId(id)).thenReturn(characterDto);
        CharacterDto result = defaultCharacterManager.readCharacterByUserId(id);

        // then
        Assertions.assertEquals("admin", characterDto.getCharacterName());
    }

    @Test
    @DisplayName("Character 목록 조회 성공 테스트")
    public void READ_CHARACTER_LIST_SUCCESS_TEST(){
        // given
        CharacterDto characterDto1 = CharacterDto.builder()
                .characterName("1")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        CharacterDto characterDto2 = CharacterDto.builder()
                .characterName("2")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
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
    public void UPDATE_CHARACTER_SUCCESS_TEST(){
        // given
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();

        // when
        Mockito.when(characterRepository.readCharacter(afterName)).thenReturn(afterCharacter);
        defaultCharacterManager.createCharacter(beforeCharacter);
        defaultCharacterManager.updateCharacter(beforeName, afterCharacter);
        CharacterDto result = defaultCharacterManager.readCharacter(afterName);

        // then
        Assertions.assertEquals(afterName, result.getCharacterName());
    }

    @Test
    @DisplayName("Character 수정 실패 테스트 - 존재하지 않는 권한을 수정하려고 할 경우")
    public void UPDATE_CHARACTER_FAIL_UNKNOWN_PERMISSION_TEST(){
        // given
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name("unknown permission")
                                .status("fail")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(beforeCharacter);

        // then
        Assertions.assertThrows(UnknownPermissionException.class, () -> defaultCharacterManager.updateCharacter(afterName, afterCharacter));
    }

    @Test
    @DisplayName("Character 수정 실패 테스트 - 해당 권한에 해당하지 않는 permission status 로 변경하려고 시도하는 경우")
    public void UPDATE_CHARACTER_FAIL_UNKNOWN_PERMISSION_STATUS_TEST(){
        // given
        String beforeName = "mock character";
        CharacterDto beforeCharacter = CharacterDto.builder()
                .characterName(beforeName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("fail")
                                .build()
                )).build();
        String afterName = "hello world";
        CharacterDto afterCharacter = CharacterDto.builder()
                .characterName(afterName)
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name(permissionName)
                                .status("never used")
                                .build()
                )).build();

        // when
        defaultCharacterManager.createCharacter(beforeCharacter);

        // then
        Assertions.assertThrows(UnknownPermissionStatusException.class, () -> defaultCharacterManager.updateCharacter(afterName, afterCharacter));
    }

    @Test
    @DisplayName("Character 삭제 성공 테스트")
    public void DELETE_CHARACTER_SUCCESS_TEST(){
        // given
        String characterName = "characterName";

        // when & then
        Assertions.assertDoesNotThrow(() -> defaultCharacterManager.deleteCharacter(characterName));
    }

}
