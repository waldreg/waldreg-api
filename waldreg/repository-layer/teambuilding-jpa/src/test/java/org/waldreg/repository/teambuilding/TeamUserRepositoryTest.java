package org.waldreg.repository.teambuilding;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.teambuilding.repository.TestJpaCharacterRepository;
import org.waldreg.repository.teambuilding.teamuser.TeamUserRepositoryServiceProvider;
import org.waldreg.repository.teambuilding.teamuser.mapper.TeamUserRepositoryMapper;
import org.waldreg.repository.teambuilding.teamuser.repository.JpaTeamUserRepository;
import org.waldreg.teambuilding.dto.UserDto;
import org.waldreg.teambuilding.team.spi.TeamUserRepository;
import org.waldreg.teambuilding.teambuilding.spi.TeamBuildingUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {TeamUserRepositoryMapper.class, TeamUserRepositoryServiceProvider.class, JpaTeamBuildingTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class TeamUserRepositoryTest{

    @Autowired
    private TeamUserRepository teamUserRepository;
    @Autowired
    private TeamBuildingUserRepository teamBuildingUserRepository;
    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;
    @Autowired
    private JpaTeamUserRepository jpaTeamUserRepository;

    @Test
    @DisplayName("user_id로 User 조회 성공 테스트")
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
        testJpaCharacterRepository.save(character);
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);
        UserDto result = teamUserRepository.getUserInfoByUserId(storedUser.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(storedUser.getId(), result.getId()),
                () -> Assertions.assertEquals(storedUser.getName(), result.getName()),
                () -> Assertions.assertEquals(storedUser.getUserId(), result.getUserId())
        );

    }

    @Test
    @DisplayName("user_id로 User 존재 여부 확인 성공 테스트")
    public void EXISTS_USER_BY_USER_ID_SUCCESS_TEST(){
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
        testJpaCharacterRepository.save(character);
        User storedUser = jpaTeamUserRepository.saveAndFlush(user);

        //then
        Assertions.assertTrue(() -> teamUserRepository.isExistUserByUserId(storedUser.getUserId()));

    }

}
