package org.waldreg.repository.reward.user.mapper;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.waldreg.domain.user.User;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.reward.users.dto.UsersRewardTagDto;

@Component
public class RewardTagWrapperMapper{

    public UsersRewardDto userToUsersRewardDto(User user){
        return UsersRewardDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .reward(user.getRewardTagWrapperList().stream().mapToInt(u -> u.getRewardTag().getRewardPoint()).sum())
                .usersRewardTagDtoList(user.getRewardTagWrapperList().stream()
                        .map(ur -> UsersRewardTagDto.builder()
                                .rewardId(ur.getRewardId())
                                .rewardPoint(ur.getRewardTag().getRewardPoint())
                                .rewardTagTitle(ur.getRewardTag().getRewardTagTitle())
                                .rewardTagId(ur.getRewardTag().getRewardTagId())
                                .rewardPresentedAt(ur.getRewardPresentedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}
