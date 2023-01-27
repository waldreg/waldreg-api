package org.wadlreg.reward.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.exception.UnknownRewardException;
import org.wadlreg.reward.exception.UnknownRewardTargetException;
import org.wadlreg.reward.exception.UnknownRewardTagException;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.spi.repository.UserExistChecker;
import org.wadlreg.reward.users.spi.repository.UsersRewardManagerRepository;
import org.wadlreg.reward.users.spi.tag.RewardTagExistChecker;

@Service
public class DefaultUsersRewardManager implements UsersRewardManager{

    private final UsersRewardManagerRepository usersRewardManagerRepository;
    private final RewardTagExistChecker rewardTagExistChecker;
    private final UserExistChecker userExistChecker;

    @Autowired
    public DefaultUsersRewardManager(UsersRewardManagerRepository usersRewardManagerRepository,
            RewardTagExistChecker rewardTagExistChecker,
            UserExistChecker userExistChecker){
        this.usersRewardManagerRepository = usersRewardManagerRepository;
        this.rewardTagExistChecker = rewardTagExistChecker;
        this.userExistChecker = userExistChecker;
    }

    @Override
    public void assignRewardToUser(int id, int rewardTagId){
        throwIfCannotFindUserById(id);
        throwIfCannotFindRewardByRewardTagId(rewardTagId);
        usersRewardManagerRepository.assignRewardToUser(id, rewardTagId);
    }

    @Override
    public UsersRewardDto readSpecifyUsersReward(int id){
        throwIfCannotFindUserById(id);
        return usersRewardManagerRepository.readSpecifyUsersReward(id);
    }

    @Override
    public void deleteRewardToUser(int id, int rewardId){
        throwIfCannotFindUserById(id);
        throwIfCannotFindRewardOnUser(id, rewardId);
        usersRewardManagerRepository.deleteRewardToUser(id, rewardId);
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
        if(!usersRewardManagerRepository.isRewardIdExist(id, rewardId)){
            throw new UnknownRewardException(rewardId);
        }
    }

    @Override
    public void resetAllUsersReward(){
        usersRewardManagerRepository.resetAllUsersReward();
    }

}
