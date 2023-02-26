package org.waldreg.repository.reward.user.repository;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;
import org.waldreg.reward.users.spi.repository.UsersRewardRepository;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaRewardUserRepositoryTest{

    @Autowired
    private UsersRewardRepository usersRewardRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("유저에게 상 벌점 태그 부여 테스트")
    void ASSIGN_REWARD_TO_USER_TEST(){
        // given
        User user = getUser("hello");

        // when

        // then

    }

    private User getUser(String userId){
        return User.builder()
                .userId(userId)
                .userPassword(userId)
                .name(userId)
                .character(getCharacter())
                .phoneNumber("010-0000-0000")
                .build();
    }

    private Character getCharacter(){
        return Character.builder()
                .characterName("Hello world1")
                .permissionList(List.of())
                .build();
    }

    private RewardTag getRewardTag(){
        return RewardTag.builder()
                .rewardTagTitle("reward tag")
                .rewardPoint(100)
                .build();
    }

}
