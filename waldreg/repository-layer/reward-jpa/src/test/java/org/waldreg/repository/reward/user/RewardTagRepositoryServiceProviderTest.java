package org.waldreg.repository.reward.user;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;
import org.waldreg.repository.reward.JpaRewardTestInitializer;
import org.waldreg.repository.reward.repository.JpaRewardTagRepository;
import org.waldreg.repository.reward.repository.JpaRewardUserRepository;
import org.waldreg.repository.reward.repository.TestJpaCharacterRepository;
import org.waldreg.repository.reward.user.mapper.RewardTagWrapperMapper;
import org.waldreg.reward.users.dto.UsersRewardDto;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaRewardTestInitializer.class, UsersRewardRepositoryServiceProvider.class, RewardTagWrapperMapper.class})
@TestPropertySource("classpath:h2-application.properties")
class RewardTagRepositoryServiceProviderTest{

    @Autowired
    private UsersRewardRepositoryServiceProvider serviceProvider;

    @Autowired
    private JpaRewardTagRepository jpaRewardTagRepository;

    @Autowired
    private JpaRewardUserRepository jpaRewardUserRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("유저에게 상점태그 할당 및 조회 테스트")
    void ASSIGN_REWARD_TAG_TO_USER_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);

        serviceProvider.assignRewardToUser(savedUser.getId(), savedRewardTag.getRewardTagId());

        UsersRewardDto result = serviceProvider.readSpecifyUsersReward(savedUser.getId());

        // then
        assertAll(
                () -> assertEquals(savedUser.getId(), result.getId()),
                () -> assertEquals(savedUser.getUserId(), result.getUserId()),
                () -> assertEquals(savedUser.getName(), result.getName()),
                () -> assertEquals(100, result.getReward()),
                () -> assertEquals(1, result.getUsersRewardTagDtoList().size())
        );
    }

    @Test
    @DisplayName("유저에게 주어진 상점태그 삭제 테스트")
    void DELETE_REWARD_TAG_TO_USER_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);

        serviceProvider.assignRewardToUser(savedUser.getId(), savedRewardTag.getRewardTagId());

        UsersRewardDto assigned = serviceProvider.readSpecifyUsersReward(savedUser.getId());

        serviceProvider.deleteRewardToUser(assigned.getId(), assigned.getUsersRewardTagDtoList().get(0).getRewardId());

        UsersRewardDto result = serviceProvider.readSpecifyUsersReward(savedUser.getId());

        // then
        assertAll(
                () -> assertEquals(0, result.getReward()),
                () -> assertTrue(result.getUsersRewardTagDtoList().isEmpty())
        );
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
        return jpaCharacterRepository.saveAndFlush(Character.builder()
                .characterName("Hello world1")
                .permissionList(List.of())
                .build());
    }

    private RewardTag getRewardTag(){
        return RewardTag.builder()
                .rewardTagTitle("reward tag")
                .rewardPoint(100)
                .build();
    }

}
