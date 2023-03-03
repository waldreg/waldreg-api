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

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaCharacterRepositoryTest{

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("characterName으로 character 조회 성공 테스트")
    public void GET_CHARACTER_BY_CHARACTER_NAME_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);
        Character result = jpaCharacterRepository.getCharacterByCharacterName("Guest").get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(character.getCharacterName(), result.getCharacterName()),
                () -> Assertions.assertEquals(character.getPermissionList().size(),result.getPermissionList().size())
        );

    }

    @Test
    @DisplayName("characterName으로 character 존재여부 확인 성공 테스트")
    public void EXISTS_CHARACTER_BY_CHARACTER_NAME_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        jpaCharacterRepository.saveAndFlush(character);

        //then
        Assertions.assertTrue(() -> jpaCharacterRepository.existsByCharacterName("Guest"));

    }

}
