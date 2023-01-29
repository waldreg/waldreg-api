package org.wadlreg.reward.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.exception.UnknownRewardException;
import org.wadlreg.reward.exception.UnknownRewardTargetException;
import org.wadlreg.reward.exception.UnknownRewardTagException;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.spi.repository.UserExistChecker;
import org.wadlreg.reward.users.spi.repository.UsersRewardRepository;
import org.wadlreg.reward.users.spi.tag.RewardTagExistChecker;

@Service
public class DefaultUsersRewardManager implements UsersRewardManager{

    private final UsersRewardRepository usersRewardRepository;
    private final RewardTagExistChecker rewardTagExistChecker;
    private final UserExistChecker userExistChecker;

    @Autowired
    public DefaultUsersRewardManager(UsersRewardRepository usersRewardRepository,
            RewardTagExistChecker rewardTagExistChecker,
            UserExistChecker userExistChecker){
        this.usersRewardRepository = usersRewardRepository;
        this.rewardTagExistChecker = rewardTagExistChecker;
        this.userExistChecker = userExistChecker;
    }

    @Override
    public void assignRewardToUser(int id, int rewardTagId){
        throwIfCannotFindUserById(id);
        throwIfCannotFindRewardByRewardTagId(rewardTagId);
        usersRewardRepository.assignRewardToUser(id, rewardTagId);
    }

    @Override
    public UsersRewardDto readSpecifyUsersReward(int id){
        throwIfCannotFindUserById(id);
        return usersRewardRepository.readSpecifyUsersReward(id);
    }

    @Override
    public void deleteRewardToUser(int id, int rewardId){
        throwIfCannotFindUserById(id);
        throwIfCannotFindRewardOnUser(id, rewardId);
        usersRewardRepository.deleteRewardToUser(id, rewardId);
    }

    private void throwIfCannotFindUserById(int id){
        if(!userExistChecker.isUserExist(id)){
            throw new UnknownRewardTargetException(id);
        }
    }

    private void throwIfCannotFindRewardByRewardTagId(int rewardTagId){
        if(!rewardTagExistChecker.isRewardTagExist(rewardTagId)){
            throw new UnknownRewardTagException(rewardTagId);
        }
    }

    private void throwIfCannotFindRewardOnUser(int id, int rewardId){
        if(!usersRewardRepository.isRewardIdExist(id, rewardId)){
            throw new UnknownRewardException(rewardId);
        }
    }

    @Override
    public void resetAllUsersReward(){
        usersRewardRepository.resetAllUsersReward();
    }

}
