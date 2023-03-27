package org.waldreg.reward.users.spi.repository;

import java.util.List;
import org.waldreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardRepository{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    List<Integer> readUsersAssignedRewardHistory();

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

    boolean isRewardIdExist(int id, int rewardId);

}
