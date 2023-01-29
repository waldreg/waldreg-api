package org.waldreg.repository.reward;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;
import org.waldreg.repository.RewardTagStorage;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryRewardTagRepository.class, RewardTagMapper.class, RewardTagStorage.class})
public class RewardTagRepositoryTest{

    @Autowired
    private RewardTagRepository rewardTagRepository;

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

}
