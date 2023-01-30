package org.waldreg.repository.reward;

import org.springframework.stereotype.Service;
import org.waldreg.reward.tag.dto.RewardTagDto;
import org.waldreg.domain.rewardtag.RewardTag;

@Service
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
