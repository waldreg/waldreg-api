package org.waldreg.repository.character.repository;

import java.util.List;
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
    private TestJpaUserRepository userRepository;

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
                                .name("Hello world")
                                .status("true")
                                .build()
                ))
                .build();

        // when
        Character saved = repository.saveAndFlush(character);
        entityManager.clear();
        Character result = repository.findById(saved.getId()).get();

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
