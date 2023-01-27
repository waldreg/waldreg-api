package org.wadlreg.reward.users;

import org.wadlreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardManager{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

}
