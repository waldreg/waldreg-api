package org.wadlreg.reward.users.spi;

import org.wadlreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardManagerRepository{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

}
