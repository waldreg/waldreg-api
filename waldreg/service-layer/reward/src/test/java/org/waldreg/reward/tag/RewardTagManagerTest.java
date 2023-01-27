package org.waldreg.reward.tag;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wadlreg.reward.tag.DefaultRewardTagManager;
import org.wadlreg.reward.tag.RewardTagManager;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultRewardTagManager.class})
public class RewardTagManagerTest{

    @Autowired
    private RewardTagManager rewardTagManager;

    @MockBean
    private RewardTagRepository rewardTagRepository;

    @Test
    @DisplayName("새로운 RewardTag 생성 성공 테스트")
    public void CREATE_NEW_REWARD_TAG_SUCCESS_TEST(){
        // given
        String rewardTagTitle = "hello reward";
        int rewardPoint = 10;
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when & then
        Assertions.assertDoesNotThrow(() -> rewardTagManager.createRewardTag(rewardTagDto));
    }

    @Test
    @DisplayName("RewardTag 업데이트 성공 테스트")
    public void UPDATE_REWARD_TAG_SUCCESS_TEST(){
        // given
        String rewardTagTitle = "hello reward";
        int rewardPoint = 10;
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when & then
        Assertions.assertDoesNotThrow(() -> rewardTagManager.updateRewardTag(1, rewardTagDto));
    }

    @Test
    @DisplayName("RewardTag 삭제 삭제 성공 테스트")
    public void DELETE_REWARD_TAG_SUCCESS_TEST(){
        // given
        int rewardTagId = 1;

        // when & then
        Assertions.assertDoesNotThrow(()->rewardTagManager.deleteRewardTag(rewardTagId));
    }

}
