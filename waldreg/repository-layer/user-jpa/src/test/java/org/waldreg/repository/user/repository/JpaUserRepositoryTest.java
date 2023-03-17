package org.waldreg.repository.user.repository;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaUserRepositoryTest{

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("새로운 유저 생성 테스트")
    void CREATE_NEW_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        User result = jpaUserRepository.saveAndFlush(user);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getName(), result.getName()),
                () -> Assertions.assertEquals(user.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(user.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(user.getCharacter().getId(), result.getCharacter().getId()),
                () -> Assertions.assertEquals(user.getCharacter().getCharacterName(), result.getCharacter().getCharacterName()),
                () -> Assertions.assertEquals(user.getCharacter().getPermissionList().size(), result.getCharacter().getPermissionList().size())
        );

    }

    @Test
    @DisplayName("id로 특정 유저 조회 테스트")
    void READ_USER_BY_ID_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        user = jpaUserRepository.saveAndFlush(user);
        entityManager.clear();
        User result = jpaUserRepository.findById(user.getId()).get();

        //then
        assertAll(user, result);

    }

    @Test
    @DisplayName("유저 정보 수정 테스트")
    void UPDATE_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        User user2 = jpaUserRepository.saveAndFlush(user);
        user2.setName("lini");
        user2.setUserPassword("2ghkdhkdhk!");
        entityManager.flush();
        entityManager.clear();
        User result = jpaUserRepository.findById(user2.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user2.getId(), result.getId()),
                () -> Assertions.assertEquals(user2.getName(), result.getName()),
                () -> Assertions.assertEquals(user2.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(user2.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(user2.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(user2.getCharacter().getId(), result.getCharacter().getId()),
                () -> Assertions.assertEquals(user2.getCharacter().getCharacterName(), result.getCharacter().getCharacterName()),
                () -> Assertions.assertEquals(user2.getCharacter().getPermissionList().size(), result.getCharacter().getPermissionList().size())
        );

    }

    @Test
    @DisplayName("유저 역할 수정 테스트")
    void UPDATE_USER_CHARACTER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();
        Character modifiedCharacter = Character.builder()
                .characterName("temp_character")
                .permissionList(List.of())
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        jpaCharacterRepository.saveAndFlush(modifiedCharacter);
        User user2 = jpaUserRepository.saveAndFlush(user);
        user2.setCharacter(modifiedCharacter);
        entityManager.flush();
        entityManager.clear();
        User result = jpaUserRepository.findById(user2.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user2.getId(), result.getId()),
                () -> Assertions.assertEquals(user2.getName(), result.getName()),
                () -> Assertions.assertEquals(user2.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(user2.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(user2.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(modifiedCharacter.getId(), result.getCharacter().getId()),
                () -> Assertions.assertEquals(modifiedCharacter.getCharacterName(), result.getCharacter().getCharacterName()),
                () -> Assertions.assertEquals(modifiedCharacter.getPermissionList().size(), result.getCharacter().getPermissionList().size())
        );

    }

    @Test
    @DisplayName("id로 유저 삭제 테스트")
    void DELETE_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        user = jpaUserRepository.saveAndFlush(user);
        jpaUserRepository.deleteById(user.getId());

        //then
        Assertions.assertFalse(jpaUserRepository.existsById(user.getId()));

    }


    private void assertAll(User expected, User actual){
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected.getId(), actual.getId()),
                () -> Assertions.assertEquals(expected.getName(), actual.getName()),
                () -> Assertions.assertEquals(expected.getUserId(), actual.getUserId()),
                () -> Assertions.assertEquals(expected.getUserPassword(), actual.getUserPassword()),
                () -> Assertions.assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber()),
                () -> Assertions.assertEquals(expected.getCharacter().getId(), actual.getCharacter().getId()),
                () -> Assertions.assertEquals(expected.getCharacter().getCharacterName(), actual.getCharacter().getCharacterName()),
                () -> Assertions.assertEquals(expected.getCharacter().getPermissionList().size(), actual.getCharacter().getPermissionList().size())
        );
    }

    @Test
    @DisplayName("user_id로 유저 존재 여부 테스트")
    void CHECK_USER_EXISTS_BY_USER_ID_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        User user = User.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character(character)
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        user = jpaUserRepository.saveAndFlush(user);
        entityManager.clear();

        //then
        Assertions.assertTrue(jpaUserRepository.existsByUserInfoUserId(user.getUserId()));

    }

}
