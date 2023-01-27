package org.wadlreg.reward.users;

import org.wadlreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardTagManager{

    void assignRewardTagToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    void deleteRewardToUser(int id, int rewardId);

}
