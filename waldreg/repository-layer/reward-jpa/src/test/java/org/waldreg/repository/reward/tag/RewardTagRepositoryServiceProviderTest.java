package org.waldreg.repository.reward.tag;

import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.repository.reward.JpaRewardTestInitializer;
import org.waldreg.repository.reward.tag.mapper.RewardTagMapper;
import org.waldreg.reward.tag.dto.RewardTagDto;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaRewardTestInitializer.class, RewardTagMapper.class, RewardTagRepositoryServiceProvider.class})
@TestPropertySource("classpath:h2-application.properties")
class RewardTagRepositoryServiceProviderTest{

    @Autowired
    private RewardTagRepositoryServiceProvider serviceProvider;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("새로운 RewardTag 생성 및 조회 테스트")
    void CREATE_NEW_REWARD_TAG_TEST(){
        // given
        RewardTagDto expected = RewardTagDto.builder()
                .rewardTagTitle("Hello world")
                .rewardPoint(1000)
                .build();

        // when
        serviceProvider.createRewardTag(expected);

        entityManager.clear();

        RewardTagDto result = serviceProvider.readRewardTagList().get(0);

        // then
        assertRewardTagDto(expected, result);
    }

    @Test
    @DisplayName("RewardTag 업데이트 및 조회 테스트")
    void UPDATE_REWARD_TAG_TEST(){
        // given
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle("Hello world")
                .rewardPoint(1000)
                .build();

        RewardTagDto updated = RewardTagDto.builder()
                .rewardTagTitle("Updated")
                .rewardPoint(-1000)
                .build();

        // when
        serviceProvider.createRewardTag(rewardTagDto);

        RewardTagDto saved = serviceProvider.readRewardTagList().get(0);
        serviceProvider.updateRewardTag(saved.getRewardTagId(), updated);

        RewardTagDto result = serviceProvider.readRewardTagList().get(0);

        // then
        assertRewardTagDto(updated, result);
    }

    @Test
    @DisplayName("RewardTag 삭제 테스트")
    void DELETE_REWARD_TAG_TEST(){
        // given
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle("Hello world")
                .rewardPoint(1000)
                .build();

        // when
        serviceProvider.createRewardTag(rewardTagDto);

        RewardTagDto saved = serviceProvider.readRewardTagList().get(0);
        serviceProvider.deleteRewardTag(saved.getRewardTagId());

        List<RewardTagDto> result = serviceProvider.readRewardTagList();

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("RewardTag 존재 확인 테스트")
    void REWARD_TAG_EXIST_TEST(){
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle("Hello world")
                .rewardPoint(1000)
                .build();

        // when
        serviceProvider.createRewardTag(rewardTagDto);

        RewardTagDto saved = serviceProvider.readRewardTagList().get(0);

        // then
        assertTrue(serviceProvider.isRewardTagExist(saved.getRewardTagId()));
    }

    private void assertRewardTagDto(RewardTagDto expected, RewardTagDto result){
        assertAll(
                () -> assertTrue(result.getRewardTagId() > 0),
                () -> assertEquals(expected.getRewardTagTitle(), result.getRewardTagTitle()),
                () -> assertEquals(expected.getRewardPoint(), result.getRewardPoint())
        );
    }

}
