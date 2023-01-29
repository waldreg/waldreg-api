package org.wadlreg.reward.users.spi.repository;

import org.wadlreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardRepository{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

    boolean isRewardIdExist(int id, int rewardId);

}
