package org.waldreg.reward.users;

import java.util.List;
import org.waldreg.reward.users.dto.UsersRewardDto;

public interface UsersRewardManager{

    void assignRewardToUser(int id, int rewardTagId);

    UsersRewardDto readSpecifyUsersReward(int id);

    List<Integer> readUsersAssignedRewardHistory();

    void deleteRewardToUser(int id, int rewardId);

    void resetAllUsersReward();

}
