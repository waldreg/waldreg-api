package org.waldreg.repository.board.repository;

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
public class JpaUserRepositoryTest{

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("userId로 유저 조회")
    public void INQUIRY_USER_BY_USER_USER_ID(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        jpaCharacterRepository.save(character);
        User user = User.builder()
                .userId("Fixtar")
                .name("123")
                .userPassword("abcd")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        //when
        User result = jpaUserRepository.findByUserId("Fixtar").get();

        Assertions.assertAll(
                ()->Assertions.assertEquals(user.getUserId(),result.getUserId()),
                ()->Assertions.assertEquals(user.getUserPassword(),result.getUserPassword()),
                ()->Assertions.assertEquals(user.getName(),result.getName()),
                ()->Assertions.assertEquals(user.getPhoneNumber(),result.getPhoneNumber())
        );


    }

}
