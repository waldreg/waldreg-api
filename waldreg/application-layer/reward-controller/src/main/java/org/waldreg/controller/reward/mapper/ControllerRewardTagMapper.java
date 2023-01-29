package org.waldreg.controller.reward.mapper;

import org.springframework.stereotype.Service;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.waldreg.controller.reward.request.RewardTagRequest;

@Service
public class ControllerRewardTagMapper{

    public RewardTagDto rewardTagRequestToRewardTagDto(RewardTagRequest rewardTagRequest){
        return RewardTagDto.builder()
                .rewardTagTitle(rewardTagRequest.getRewardTagTitle())
                .rewardPoint(rewardTagRequest.getRewardPoint())
                .build();
    }

}
