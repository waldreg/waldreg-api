package org.wadlreg.reward.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.spi.UsersRewardManagerRepository;

@Service
public class DefaultUsersRewardManager implements UsersRewardManager{

    private final UsersRewardManagerRepository usersRewardManagerRepository;

    @Autowired
    public DefaultUsersRewardManager(UsersRewardManagerRepository usersRewardManagerRepository){
        this.usersRewardManagerRepository = usersRewardManagerRepository;
    }

    @Override
    public void assignRewardToUser(int id, int rewardTagId){
        usersRewardManagerRepository.assignRewardToUser(id, rewardTagId);
    }

    @Override
    public UsersRewardDto readSpecifyUsersReward(int id){
        return usersRewardManagerRepository.readSpecifyUsersReward(id);
    }

    @Override
    public void deleteRewardToUser(int id, int rewardId){
        usersRewardManagerRepository.deleteRewardToUser(id, rewardId);
    }

    @Override
    public void resetAllUsersReward(){
        usersRewardManagerRepository.resetAllUsersReward();
    }

}
