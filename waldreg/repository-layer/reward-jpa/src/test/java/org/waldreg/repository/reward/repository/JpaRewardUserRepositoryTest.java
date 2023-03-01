package org.waldreg.repository.reward.repository;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaRewardUserRepositoryTest{

    @Autowired
    private JpaRewardUserRepository jpaRewardUserRepository;

    @Autowired
    private JpaRewardTagRepository jpaRewardTagRepository;

    @Autowired
    private JpaRewardTagWrapperRepository jpaRewardTagWrapperRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("유저에게 상 벌점 태그 부여 및 조회 테스트")
    void ASSIGN_REWARD_TO_USER_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);

        savedUser.addRewardTag(savedRewardTag);

        entityManager.flush();
        entityManager.clear();

        User result = jpaRewardUserRepository.findById(savedUser.getId()).get();

        // then
        assertAll(
                () -> assertEquals(1, result.getRewardTagWrapperList().size()),
                () -> assertRewardTag(savedRewardTag, result.getRewardTagWrapperList().get(0).getRewardTag())
        );
    }

    @Test
    @DisplayName("유저 상 벌점 삭제 테스트")
    void DELETE_REWARD_TO_USER_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);
        savedUser.addRewardTag(savedRewardTag);

        entityManager.flush();
        entityManager.clear();

        User temp = jpaRewardUserRepository.findById(savedUser.getId()).get();
        temp.deleteRewardTag(temp.getRewardTagWrapperList().get(0).getRewardId());

        entityManager.flush();
        entityManager.clear();

        User result = jpaRewardUserRepository.findById(savedUser.getId()).get();

        // then
        assertTrue(result.getRewardTagWrapperList().isEmpty());
    }

    @Test
    @DisplayName("모든 유저의 상 벌점 초기화 테스트")
    void DELETE_ALL_REWARD_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);
        savedUser.addRewardTag(savedRewardTag);

        jpaRewardTagWrapperRepository.deleteAllWrapper();

        entityManager.flush();
        entityManager.clear();

        User result = jpaRewardUserRepository.findById(savedUser.getId()).get();

        // then
        assertTrue(result.getRewardTagWrapperList().isEmpty());
    }

    @Test
    @DisplayName("rewardId 존재 확인 테스트")
    void EXIST_CHECK_REWARD_ID_TEST(){
        // given
        User user = getUser("hello");
        RewardTag rewardTag = getRewardTag();

        // when
        User savedUser = jpaRewardUserRepository.saveAndFlush(user);
        RewardTag savedRewardTag = jpaRewardTagRepository.saveAndFlush(rewardTag);
        savedUser.addRewardTag(savedRewardTag);

        entityManager.flush();

        User result = jpaRewardUserRepository.findById(savedUser.getId()).get();

        // then
        assertTrue(jpaRewardTagWrapperRepository.existsById(result.getRewardTagWrapperList().get(0).getRewardId()));
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

    private boolean assertRewardTag(RewardTag expected, RewardTag result){
        assertAll(
                () -> assertEquals(expected.getRewardTagTitle(), result.getRewardTagTitle()),
                () -> assertEquals(expected.getRewardPoint(), result.getRewardPoint())
        );
        return true;
    }

}
