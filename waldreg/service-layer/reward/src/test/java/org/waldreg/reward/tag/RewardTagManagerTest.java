package org.waldreg.reward.tag;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
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

    @Test
    @DisplayName("RewardTag 조회 성공 테스트")
    public void READ_REWARD_TAG_SUCCESS_TEST(){
        // given
        String rewardTagTitle = "hello reward";
        int rewardPoint = 10;
        RewardTagDto rewardTagDto = RewardTagDto.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        String rewardTagTitle2 = "hello reward2";
        int rewardPoint2 = -10;
        RewardTagDto rewardTagDto2 = RewardTagDto.builder()
                .rewardTagTitle(rewardTagTitle2)
                .rewardPoint(rewardPoint2)
                .build();


        // when
        Mockito.when(rewardTagRepository.readRewardTagList()).thenReturn(List.of(rewardTagDto, rewardTagDto2));
        List<RewardTagDto> rewardTagDtoList = rewardTagRepository.readRewardTagList();

        // then
        Assertions.assertAll(
                ()-> Assertions.assertEquals(rewardTagDtoList.get(0).getRewardTagTitle(), rewardTagTitle),
                ()-> Assertions.assertEquals(rewardTagDtoList.get(0).getRewardPoint(), rewardPoint),
                ()-> Assertions.assertEquals(rewardTagDtoList.get(1).getRewardTagTitle(), rewardTagTitle2),
                ()-> Assertions.assertEquals(rewardTagDtoList.get(1).getRewardPoint(), rewardPoint2)
        );
    }

}
