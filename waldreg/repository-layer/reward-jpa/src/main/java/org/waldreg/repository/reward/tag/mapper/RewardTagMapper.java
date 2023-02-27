package org.waldreg.repository.reward.tag.mapper;

import org.springframework.stereotype.Component;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.reward.tag.dto.RewardTagDto;

@Component
public class RewardTagMapper{

    public RewardTag rewardTagDtoToRewardTag(RewardTagDto rewardTagDto){
        return RewardTag.builder()
                .rewardTagTitle(rewardTagDto.getRewardTagTitle())
                .rewardPoint(rewardTagDto.getRewardPoint())
                .build();
    }

    public RewardTagDto rewardTagToRewardTagDto(RewardTag rewardTag){
        return RewardTagDto.builder()
                .rewardTagId(rewardTag.getRewardTagId())
                .rewardTagTitle(rewardTag.getRewardTagTitle())
                .rewardPoint(rewardTag.getRewardPoint())
                .build();
    }

}
