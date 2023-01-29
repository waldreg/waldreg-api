package org.waldreg.controller.reward.users.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.dto.UsersRewardTagDto;
import org.waldreg.controller.reward.users.response.RewardTagWrapperResponse;
import org.waldreg.controller.reward.users.response.UsersRewardTagResponse;

@Service
public class ControllerUsersRewardMapper{

    public UsersRewardTagResponse usersRewardDtoToUsersRewardTagResponse(UsersRewardDto usersRewardDto){
        return UsersRewardTagResponse.builder()
                .name(usersRewardDto.getName())
                .userId(usersRewardDto.getUserId())
                .id(usersRewardDto.getId())
                .reward(usersRewardDto.getReward())
                .rewardInfoList(usersRewardTagDtoListToRewardTagWrapperResponseList(usersRewardDto.getUsersRewardTagDtoList()))
                .build();
    }

    private List<RewardTagWrapperResponse> usersRewardTagDtoListToRewardTagWrapperResponseList(List<UsersRewardTagDto> usersRewardTagDtoList){
        List<RewardTagWrapperResponse> rewardTagWrapperResponseList = new ArrayList<>();
        for(UsersRewardTagDto usersRewardTagDto : usersRewardTagDtoList){
            rewardTagWrapperResponseList.add(RewardTagWrapperResponse.builder()
                            .rewardId(usersRewardTagDto.getRewardId())
                            .rewardTagId(usersRewardTagDto.getRewardTagId())
                            .rewardTagTitle(usersRewardTagDto.getRewardTagTitle())
                            .rewardPoint(usersRewardTagDto.getRewardPoint())
                            .rewardPresentedAt(usersRewardTagDto.getRewardPresentedAt().toString())
                    .build());
        }
        return rewardTagWrapperResponseList;
    }

}
