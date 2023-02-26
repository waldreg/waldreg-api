package org.waldreg.repository.character;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;
import org.waldreg.domain.user.User;
import org.waldreg.repository.character.mapper.CharacterRepositoryMapper;
import org.waldreg.repository.character.repository.JpaCharacterRepository;
import org.waldreg.repository.character.repository.JpaUserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {CharacterRepositoryServiceProvider.class,
        CharacterRepositoryMapper.class,
        JpaCharacterTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
class CharacterRepositoryTest{

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("새로운 Character 생성 테스트")
    void CREATE_NEW_CHARACTER_TEST(){
        // given
        int permissionUnitId = 1;

        CharacterDto characterDto = CharacterDto.builder()
                .characterName("Hello world")
                .permissionDtoList(
                        List.of(
                            PermissionDto.builder()
                                .service("Test")
                                .id(permissionUnitId)
                                .name("Hello world permission")
                                .status("false")
                                .build()
                        )
                ).build();

        // when & then
        Assertions.assertDoesNotThrow(() -> characterRepository.createCharacter(characterDto));
    }

    @Test
    @DisplayName("CHARACTER 조회 테스트")
    void READ_CHARACTER_TEST(){
        // given
        int permissionUnitId = 1;
        String name = "Hello";

        CharacterDto characterDto = CharacterDto.builder()
                .characterName(name)
                .permissionDtoList(
                        List.of(
                                PermissionDto.builder()
                                        .service("Test")
                                        .id(permissionUnitId)
                                        .name("Hello world permission")
                                        .status("false")
                                        .build()
                        )
                ).build();

        // when
        characterRepository.createCharacter(characterDto);
        CharacterDto result = characterRepository.readCharacter(name).orElseThrow(IllegalStateException::new);

        // then
        assertSameCharacter(characterDto, result);
    }

    @Test
    @DisplayName("유저의 아이디로 CHARACTER 조회 테스트")
    void READ_CHARACTER_BY_USERS_ID_TEST(){
        // given
        CharacterDto expected = CharacterDto.builder()
                .characterName("Hello world")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("Test")
                                .id(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();
        int id = initUserAndGetId();

        // when
        CharacterDto result = characterRepository.readCharacterByUserId(id);

        // then
        assertSameCharacter(expected, result);
    }

    @Test
    @DisplayName("모든 CHARACTER 조회 테스트")
    void READ_ALL_CHARACTER_TEST(){
        // given
        CharacterDto expected1 = CharacterDto.builder()
                .characterName("Hello world1")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("Test")
                                .id(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        CharacterDto expected2 = CharacterDto.builder()
                .characterName("Hello world2")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("Test")
                                .id(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        CharacterDto expected3 = CharacterDto.builder()
                .characterName("Hello world3")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("Test")
                                .id(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        characterRepository.createCharacter(expected1);
        characterRepository.createCharacter(expected2);
        characterRepository.createCharacter(expected3);

        List<CharacterDto> resultList = characterRepository.readCharacterList();

        // then
        resultList.stream()
                .filter(r -> r.getCharacterName().equals("Hello world1"))
                .findFirst()
                .ifPresentOrElse(r -> assertSameCharacter(expected1, r), () -> {throw new IllegalStateException();});

        resultList.stream()
                .filter(r -> r.getCharacterName().equals("Hello world2"))
                .findFirst()
                .ifPresentOrElse(r -> assertSameCharacter(expected2, r), () -> {throw new IllegalStateException();});

        resultList.stream()
                .filter(r -> r.getCharacterName().equals("Hello world3"))
                .findFirst()
                .ifPresentOrElse(r -> assertSameCharacter(expected3, r), () -> {throw new IllegalStateException();});
    }

    @Test
    @DisplayName("CHARACTER UPDATE 테스트")
    void UPDATE_CHARACTER_TEST(){
        // given
        CharacterDto saved = CharacterDto.builder()
                .characterName("saved")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("saved")
                                .id(1)
                                .name("saved")
                                .status("true")
                                .build()
                ))
                .build();

        CharacterDto expected = CharacterDto.builder()
                .characterName("expected")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("saved")
                                .id(1)
                                .name("saved")
                                .status("false")
                                .build()
                ))
                .build();

        // when
        characterRepository.createCharacter(saved);
        characterRepository.updateCharacter(saved.getCharacterName(), expected);
        CharacterDto result = characterRepository.readCharacter(expected.getCharacterName()).orElseThrow(IllegalStateException::new);

        // then
        assertSameCharacter(expected, result);
    }

    @Test
    @DisplayName("CHARACTER 삭제 테스트")
    void DELETE_CHARACTER_TEST(){
        // given
        CharacterDto saved = CharacterDto.builder()
                .characterName("saved")
                .permissionDtoList(List.of(
                        PermissionDto.builder()
                                .service("saved")
                                .id(1)
                                .name("saved")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        characterRepository.createCharacter(saved);
        characterRepository.deleteCharacter(saved.getCharacterName());

        // then
        Assertions.assertFalse(characterRepository.readCharacter(saved.getCharacterName()).isPresent());
    }

    private void assertSameCharacter(CharacterDto expected, CharacterDto result){
        assertAll(
                () -> assertNotEquals(0, result.getId()),
                () -> assertEquals(expected.getCharacterName(), result.getCharacterName()),
                () -> assertEquals(expected.getPermissionList().size(), result.getPermissionList().size()),
                () -> expected.getPermissionList().forEach(
                        e -> result.getPermissionList().stream().filter(r -> r.getName().equals(e.getName()))
                                .findFirst().ifPresentOrElse(r -> assertAll(
                                        () -> assertEquals(e.getId(), r.getId()),
                                        () -> assertEquals(e.getStatus(), r.getStatus()),
                                        () -> assertEquals(e.getName(), r.getName()),
                                        () -> assertEquals(e.getService(), r.getService())
                                ), () -> {throw new IllegalStateException();})
                )
        );
    }

    private int initUserAndGetId(){
        Character character = Character.builder()
                .characterName("Hello world")
                .permissionList(List.of(
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        User user = User.builder()
                .name("hello")
                .userId("world")
                .userPassword("1234")
                .phoneNumber("010-0000-0000")
                .character(character)
                .build();

        jpaCharacterRepository.save(character);
        return jpaUserRepository.save(user).getId();
    }

}
