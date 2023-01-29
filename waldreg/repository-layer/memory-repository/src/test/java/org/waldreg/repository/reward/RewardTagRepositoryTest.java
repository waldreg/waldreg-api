package org.waldreg.repository.reward;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;
import org.waldreg.repository.MemoryRewardTagStorage;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryRewardTagRepository.class, RewardTagMapper.class, MemoryRewardTagStorage.class})
public class RewardTagRepositoryTest{

    @Autowired
    private RewardTagRepository rewardTagRepository;

    @Autowired
    private MemoryRewardTagStorage memoryRewardTagStorage;

    @BeforeEach
    @AfterEach
    public void INIT_REWARD_TAG_STORAGE(){
        memoryRewardTagStorage.deleteAll();
    }

    @Test
    @DisplayName("새로운 상벌점 태그 생성 테스트")
    public void CREATE_NEW_REWARD_TAG_SUCCESS_TEST(){
        // given
        RewardTagDto rewardCreateRequest = RewardTagDto.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(100)
                .build();

        // when & then
        Assertions.assertDoesNotThrow(() -> rewardTagRepository.createRewardTag(rewardCreateRequest));
    }

    @Test
    @DisplayName("상벌점 조회 성공 테스트")
    public void READ_REWARD_TAG_SUCCESS_TEST(){
        // given
        RewardTagDto rewardCreateRequest = RewardTagDto.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(100)
                .build();

        // when
        rewardTagRepository.createRewardTag(rewardCreateRequest);
        List<RewardTagDto> result = rewardTagRepository.readRewardTagList();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(rewardCreateRequest.getRewardTagTitle(), result.get(0).getRewardTagTitle()),
                () -> Assertions.assertEquals(rewardCreateRequest.getRewardPoint(), result.get(0).getRewardPoint()),
                () -> Assertions.assertTrue(result.get(0).getRewardTagId() > 0)
        );
    }

    @Test
    @DisplayName("상벌점 존재 확인 테스트")
    public void REWARD_EXIST_CHECK_SUCCESS_TEST(){
        // given
        RewardTagDto rewardCreateRequest = RewardTagDto.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(100)
                .build();

        // when
        rewardTagRepository.createRewardTag(rewardCreateRequest);
        List<RewardTagDto> result = rewardTagRepository.readRewardTagList();

        Assertions.assertAll(
                () -> Assertions.assertTrue(rewardTagRepository.isRewardTagExist(result.get(0).getRewardTagId())),
                () -> Assertions.assertFalse(rewardTagRepository.isRewardTagExist(100))
        );
    }

    @Test
    @DisplayName("상벌점 업데이트 테스트")
    public void UPDATE_REWARD_SUCCESS_TEST(){
        RewardTagDto rewardCreateRequest = RewardTagDto.builder()
                .rewardTagTitle("hello world")
                .rewardPoint(100)
                .build();

        RewardTagDto rewardUpdateRequest = RewardTagDto.builder()
                .rewardTagTitle("hello world too")
                .rewardPoint(-100)
                .build();

        // when
        rewardTagRepository.createRewardTag(rewardCreateRequest);
        int updateTarget = rewardTagRepository.readRewardTagList().get(0).getRewardTagId();
        rewardTagRepository.updateRewardTag(updateTarget, rewardUpdateRequest);
        List<RewardTagDto> result = rewardTagRepository.readRewardTagList();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(updateTarget, result.get(0).getRewardTagId()),
                () -> Assertions.assertEquals(rewardUpdateRequest.getRewardTagTitle(), result.get(0).getRewardTagTitle()),
                () -> Assertions.assertEquals(rewardUpdateRequest.getRewardPoint(), result.get(0).getRewardPoint())
        );
    }

}
