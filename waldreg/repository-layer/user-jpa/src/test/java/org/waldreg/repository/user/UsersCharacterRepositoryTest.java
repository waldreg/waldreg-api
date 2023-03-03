package org.waldreg.repository.user;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.repository.user.repository.JpaCharacterRepository;
import org.waldreg.user.spi.UsersCharacterRepository;

@DataJpaTest
@ContextConfiguration(classes = {UsersCharacterRepository.class, UsersCharacterRepositoryServiceProvider.class, JpaUserTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class UsersCharacterRepositoryTest{

    @Autowired
    private UsersCharacterRepository usersCharacterRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Test
    @DisplayName("characterName으로 character 존재여부 확인 성공 테스트")
    public void EXISTS_CHARACTER_BY_CHARACTER_NAME_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        //when
        jpaCharacterRepository.save(character);

        //then
        Assertions.assertTrue(() -> usersCharacterRepository.isExistCharacterName("Guest"));

    }

}
