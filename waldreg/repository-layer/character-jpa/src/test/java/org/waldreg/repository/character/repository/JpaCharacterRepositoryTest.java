package org.waldreg.repository.character.repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;
import org.waldreg.domain.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaCharacterRepositoryTest{

    @Autowired
    private JpaCharacterRepository repository;

    @Autowired
    private JpaPermissionRepository permissionRepository;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("Character 생성 테스트")
    void SAVE_CHARACTER_TEST(){
        // given
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

        // when
        Character result = repository.saveAndFlush(character);

        // then
        isAllSaved(character, result);
    }

    @Test
    @DisplayName("Character 생성 및 조회 테스트")
    void SAVE_CHARACTER_AND_READ_TEST(){
        // given
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

        // when
        Character saved = repository.saveAndFlush(character);
        entityManager.clear();
        Character result = repository.findByCharacterName(saved.getCharacterName()).get();

        // then
        isAllSaved(saved, result);
    }

    @Test
    @DisplayName("User의 id로 Character 조회 테스트")
    void SAVE_USER_AND_READ_CHARACTER_TEST(){
        // given
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

        // when
        Character savedCharacter = repository.saveAndFlush(character);
        User savedUser = userRepository.saveAndFlush(user);

        entityManager.clear();

        Character result = repository.findByUserId(savedUser.getId()).get();

        // then
        isAllSaved(savedCharacter, result);
    }

    @Test
    @DisplayName("모든 Character 조회 성공 테스트")
    void SAVE_CHARACTER_LIST_AND_READ_CHARACTER_TEST(){
        // given
        Character character1 = Character.builder()
                .characterName("Hello world1")
                .permissionList(List.of(
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();
        Character character2 = Character.builder()
                .characterName("Hello world2")
                .permissionList(List.of(
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(1)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        repository.saveAllAndFlush(List.of(character1, character2));

        entityManager.clear();

        List<Character> result = repository.findAll();

        // then
        result.stream()
                .filter(r -> r.getCharacterName().equals("Hello world1"))
                .findFirst()
                .ifPresentOrElse(r -> isAllSaved(character1, r),
                        () -> {throw new IllegalStateException("Cannot find Character \"Hello world1\"");});

        result.stream()
                .filter(r -> r.getCharacterName().equals("Hello world2"))
                .findFirst()
                .ifPresentOrElse(r -> isAllSaved(character2, r),
                        () -> {throw new IllegalStateException("Cannot find Character \"Hello world2\"");});
    }

    @Test
    @DisplayName("Character 수정 성공 테스트")
    void UPDATE_CHARACTER_TEST(){
        // given
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

        // when
        Character expected = repository.saveAndFlush(character);
        expected.setCharacterName("changed");

        entityManager.flush();
        entityManager.clear();

        Character result = repository.findByCharacterName(expected.getCharacterName()).get();

        // then
        isAllSaved(expected, result);
    }

    @Test
    @DisplayName("Permission 수정 성공 테스트")
    void UPDATE_PERMISSION_TEST(){
        // given
        int permissionUnitId = 1;

        Character character = Character.builder()
                .characterName("Hello world")
                .permissionList(List.of(
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(permissionUnitId)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        Character expected = repository.saveAndFlush(character);
        expected.getPermissionList().forEach(
                p -> character.getPermissionList().stream()
                        .filter(cp -> Objects.equals(p.getPermissionUnitId(), cp.getPermissionUnitId()))
                        .findFirst()
                        .ifPresent(cp -> cp.setPermissionStatus("false"))
        );


        entityManager.flush();
        entityManager.clear();

        Character result = repository.findByCharacterName(expected.getCharacterName()).get();

        // then
        isAllSaved(expected, result);
        result.getPermissionList().stream().filter(p -> p.getPermissionUnitId() == permissionUnitId).findFirst()
                .ifPresentOrElse(
                        p -> assertEquals("false", p.getStatus()),
                        () -> {throw new IllegalStateException();}
                );
    }

    @Test
    @DisplayName("CHARACTER 삭제 성공 테스트")
    void DELETE_PERMISSION_TEST(){
        // given
        int permissionUnitId = 1;

        Character character = Character.builder()
                .characterName("Hello world")
                .permissionList(List.of(
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(permissionUnitId)
                                .name("Hello world")
                                .status("true")
                                .build(),
                        Permission.builder()
                                .service("Test")
                                .permissionUnitId(permissionUnitId)
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        Character expected = repository.saveAndFlush(character);

        entityManager.clear();

        permissionRepository.deleteByCharacterId(expected.getId());
        repository.deleteById(expected.getId());

        entityManager.flush();
        entityManager.clear();

        Optional<Character> result = repository.findByCharacterName(expected.getCharacterName());

        // then
        assertFalse(result.isPresent());
    }

    private void isAllSaved(Character expected, Character result){
        assertAll(
                () -> assertNotNull(result.getId()),
                () -> assertEquals(expected.getId(), result.getId()),
                () -> assertEquals(expected.getCharacterName(), result.getCharacterName()),
                () -> assertEquals(expected.getPermissionList().size(), result.getPermissionList().size()),
                () -> assertDoesNotThrow(() -> result.getPermissionList().forEach(rp -> assertNotNull(rp.getId())))
        );
    }

}

