package org.waldreg.repository.usersreward;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.reward.users.dto.UsersRewardTagDto;
import org.waldreg.domain.rewardtag.RewardTagWrapper;
import org.waldreg.domain.user.User;

@Service
public class UsersRewardMapper{

    public UsersRewardDto userToUsersRewardDto(User user){
        return null;
    }

    private int getRewardPoint(List<RewardTagWrapper> rewardTagWrapperList){
        int point = 0;
        for(RewardTagWrapper rewardTagWrapper : rewardTagWrapperList){
            point += rewardTagWrapper.getRewardTag().getRewardPoint();
        }
        return point;
    }

    private List<UsersRewardTagDto> rewardTagWrapperListToUsersRewardTagDtoList(List<RewardTagWrapper> rewardTagWrapperList){
        List<UsersRewardTagDto> usersRewardTagDtoList = new ArrayList<>();
        for(RewardTagWrapper rewardTagWrapper : rewardTagWrapperList){
            usersRewardTagDtoList.add(UsersRewardTagDto.builder()
                    .rewardId(rewardTagWrapper.getRewardId())
                    .rewardPresentedAt(rewardTagWrapper.getRewardPresentedAt())
                    .rewardTagId(rewardTagWrapper.getRewardTag().getRewardTagId())
                    .rewardPoint(rewardTagWrapper.getRewardTag().getRewardPoint())
                    .rewardTagTitle(rewardTagWrapper.getRewardTag().getRewardTagTitle())
                    .build());
        }
        return usersRewardTagDtoList;
    }

}
