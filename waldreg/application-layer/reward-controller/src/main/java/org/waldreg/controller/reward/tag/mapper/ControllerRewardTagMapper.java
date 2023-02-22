package org.waldreg.controller.reward.tag.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.reward.tag.dto.RewardTagDto;
import org.waldreg.controller.reward.tag.request.RewardTagRequest;
import org.waldreg.controller.reward.tag.response.RewardTagResponse;

@Service
public class ControllerRewardTagMapper{

    public RewardTagDto rewardTagRequestToRewardTagDto(RewardTagRequest rewardTagRequest){
        return RewardTagDto.builder()
                .rewardTagTitle(rewardTagRequest.getRewardTagTitle())
                .rewardPoint(rewardTagRequest.getRewardPoint())
                .build();
    }

    public List<RewardTagResponse> rewardTagDtoListToRewardTagResponseList(List<RewardTagDto> rewardTagDtoList){
        List<RewardTagResponse> rewardTagResponseList = new ArrayList<>();
        for(RewardTagDto rewardTagDto : rewardTagDtoList){
            rewardTagResponseList.add(RewardTagResponse.builder()
                    .rewardTagId(rewardTagDto.getRewardTagId())
                    .rewardTagTitle(rewardTagDto.getRewardTagTitle())
                    .rewardPoint(rewardTagDto.getRewardPoint())
                    .build());
        }
        return rewardTagResponseList;
    }

}
