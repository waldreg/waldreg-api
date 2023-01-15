package org.waldreg.repository.character;

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
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.DuplicatedCharacterException;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryCharacterStorage;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryCharacterRepository.class, CharacterMapper.class, MemoryCharacterStorage.class})
public class CharacterRepositoryTest{

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    public void deleteAllCharacter(){
        memoryCharacterStorage.deleteAllCharacter();
    }

    @Test
    @DisplayName("새로운 역할 저장 성공 테스트")
    public void CREATE_NEW_CHARACTER_TEST(){
        // given
        CharacterDto characterDto = CharacterDto.builder()
                .characterName("mock")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .name("permission 1")
                                .status("true")
                                .build(),
                        PermissionDto.builder()
                                .name("permission 2")
                                .status("fail")
                                .build()
                )).build();

        // when & then
        Assertions.assertDoesNotThrow(()-> characterRepository.createCharacter(characterDto));
    }

    @Test
    @DisplayName("새로운 역할 저장 실패 테스트 - 중복 역할")
    public void CREATE_NEW_CHARACTER_FAIL_DUPLICATED_CHARACTER(){
        // given
        CharacterDto characterDto = CharacterDto.builder()
                .characterName("mock")
                .permissionDtoList(List.of()).build();

        CharacterDto duplicatedNameCharacterDto = CharacterDto.builder()
                .characterName("mock")
                .permissionDtoList(List.of()).build();

        // when
        characterRepository.createCharacter(characterDto);

        // then
        Assertions.assertThrows(DuplicatedCharacterException.class, ()-> characterRepository.createCharacter(duplicatedNameCharacterDto));
    }

    @Test
    @DisplayName("역할 이름기준 조회 성공 테스트")
    public void READ_CHARACTER_BY_NAME_TEST(){
        // given
        String name = "mock";
        CharacterDto characterDto = CharacterDto.builder()
                .characterName(name)
                .permissionDtoList(List.of()).build();

        // when
        characterRepository.createCharacter(characterDto);
        CharacterDto result = characterRepository.readCharacter(name);

        // then
        Assertions.assertEquals(result.getCharacterName(), characterDto.getCharacterName());
    }

    @Test
    @DisplayName("역할 이름기준 조회 실패 테스트 - 없는 역할 조회")
    public void READ_CHARACTER_BY_NAME_FAIL_UNKNOWN_CHARACTER_NAME_TEST(){
        // given
        String name = "mock";

        // when & then
        Assertions.assertThrows(UnknownCharacterException.class, ()-> characterRepository.readCharacter(name));
    }

    @Test
    @DisplayName("모든 캐릭터 조회 테스트")
    public void READ_CHARACTER_LIST_SUCCESS_TEST(){
        // given
        CharacterDto characterDto1 = CharacterDto.builder()
                .characterName("mock1")
                .permissionDtoList(List.of()).build();
        CharacterDto characterDto2 = CharacterDto.builder()
                .characterName("mock2")
                .permissionDtoList(List.of()).build();
        CharacterDto characterDto3 = CharacterDto.builder()
                .characterName("mock3")
                .permissionDtoList(List.of()).build();

        // when
        characterRepository.createCharacter(characterDto1);
        characterRepository.createCharacter(characterDto2);
        characterRepository.createCharacter(characterDto3);
        List<CharacterDto> result = characterRepository.readCharacterList();

        // then
        Assertions.assertEquals(3, result.size());
    }

}
