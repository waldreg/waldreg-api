package org.waldreg.repository.teambuilding.repository;

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
import org.waldreg.repository.teambuilding.teamuser.repository.JpaTeamUserRepository;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaTeamUserRepositoryTest{

    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("user_id로 특정 유저 조회 테스트")
    public void READ_USER_BY_USER_ID_SUCCESS_TEST(){
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
        testJpaCharacterRepository.saveAndFlush(character);
        user = jpaTeamUserRepository.saveAndFlush(user);
        entityManager.clear();
        User result = jpaTeamUserRepository.findByUserId(user.getUserId()).get();

        //then
        assertAll(user, result);

    }

    @Test
    @DisplayName("user_id로 유저 존재 여부 테스트")
    public void CHECK_USER_EXISTS_BY_USER_ID_SUCCESS_TEST(){
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
        testJpaCharacterRepository.saveAndFlush(character);
        user = jpaTeamUserRepository.saveAndFlush(user);
        entityManager.clear();

        //then
        Assertions.assertTrue(jpaTeamUserRepository.existsByUserInfoUserId(user.getUserId()));

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

}
