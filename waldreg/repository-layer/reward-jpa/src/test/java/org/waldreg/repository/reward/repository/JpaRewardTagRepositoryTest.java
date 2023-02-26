package org.waldreg.repository.reward.repository;

import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.rewardtag.RewardTag;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaRewardTagRepositoryTest{

    @Autowired
    private JpaRewardTagRepository jpaRewardTagRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("새로운 상 벌점 태그 생성 테스트")
    void CREATE_NEW_REWARD_TAG_TEST(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build();

        // when & then
        assertDoesNotThrow(() -> jpaRewardTagRepository.save(rewardTag));
    }

    @Test
    @DisplayName("상 벌점 태그 조회 테스트")
    void READ_REWARD_TAG_TEST(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build();

        // when
        RewardTag expected = jpaRewardTagRepository.saveAndFlush(rewardTag);

        entityManager.clear();

        RewardTag result = jpaRewardTagRepository.findById(expected.getRewardTagId()).get();

        // then
        assertRewardTag(expected, result);
    }

    @Test
    @DisplayName("상 벌점 태그 수정 테스트")
    void UPDATE_REWARD_TAG_TEST(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build();

        RewardTag expected = RewardTag.builder()
                .rewardTagTitle("Updated")
                .rewardPoint(-100)
                .build();

        // when
        RewardTag saved = jpaRewardTagRepository.save(rewardTag);
        RewardTag result = jpaRewardTagRepository.findById(saved.getRewardTagId()).get();

        result.updateRewardTag(expected);

        entityManager.flush();
        entityManager.clear();

        result = jpaRewardTagRepository.findById(saved.getRewardTagId()).get();

        // then
        assertRewardTag(expected, result);
    }

    @Test
    @DisplayName("상 벌점 삭제 테스트")
    void DELETE_REWARD_TAG_TEST(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build();

        // when
        RewardTag saved = jpaRewardTagRepository.saveAndFlush(rewardTag);

        entityManager.clear();

        jpaRewardTagRepository.deleteById(saved.getRewardTagId());

        Optional<RewardTag> empty = jpaRewardTagRepository.findById(saved.getRewardTagId());

        // then
        assertFalse(empty.isPresent());
    }

    @Test
    @DisplayName("모든 상 벌점 태그 조회 테스트")
    void READ_ALL_REWARD_TAG_TEST(){
        // given
        RewardTag rewardTag1 = RewardTag.builder()
                .rewardTagTitle("Test1")
                .rewardPoint(100)
                .build();
        RewardTag rewardTag2 = RewardTag.builder()
                .rewardTagTitle("Test2")
                .rewardPoint(100)
                .build();

        // when
        jpaRewardTagRepository.saveAllAndFlush(List.of(rewardTag1, rewardTag2));

        entityManager.clear();

        List<RewardTag> rewardTagList = jpaRewardTagRepository.findAll();

        // then
        rewardTagList.stream().filter(r -> r.getRewardTagTitle().equals(rewardTag1.getRewardTagTitle()))
                .findFirst().ifPresent(r -> assertRewardTag(rewardTag1, r));

        rewardTagList.stream().filter(r -> r.getRewardTagTitle().equals(rewardTag2.getRewardTagTitle()))
                .findFirst().ifPresent(r -> assertRewardTag(rewardTag2, r));
    }

    @Test
    @DisplayName("상 벌점 태그 존재 확인 테스트")
    void REWARD_TAG_EXIST_TEST(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build();

        // when
        RewardTag saved = jpaRewardTagRepository.saveAndFlush(rewardTag);

        entityManager.clear();

        boolean isExist = jpaRewardTagRepository.existsById(saved.getRewardTagId());

        // then
        assertTrue(isExist);
    }

    private void assertRewardTag(RewardTag expected, RewardTag result){
        assertAll(
                () -> assertEquals(expected.getRewardTagTitle(), result.getRewardTagTitle()),
                () -> assertEquals(expected.getRewardPoint(), result.getRewardPoint())
        );
    }

}
