package org.wadlreg.reward.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.spi.UsersRewardTagManagerRepository;

@Service
public class DefaultUsersRewardTagManager implements UsersRewardTagManager{

    private final UsersRewardTagManagerRepository usersRewardTagManagerRepository;

    @Autowired
    public DefaultUsersRewardTagManager(UsersRewardTagManagerRepository usersRewardTagManagerRepository){
        this.usersRewardTagManagerRepository = usersRewardTagManagerRepository;
    }

    @Override
    public void assignRewardTagToUser(int id, int rewardTagId){
        usersRewardTagManagerRepository.assignRewardTagToUser(id, rewardTagId);
    }

    @Override
    public UsersRewardDto readSpecifyUsersReward(int id){
        return usersRewardTagManagerRepository.readSpecifyUsersReward(id);
    }

    @Override
    public void deleteRewardToUser(int id, int rewardId){
        usersRewardTagManagerRepository.deleteRewardToUser(id, rewardId);
    }

}
