package org.waldreg.reward.users.spi.repository;

import org.waldreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardRepository{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

    boolean isRewardIdExist(int id, int rewardId);

}
