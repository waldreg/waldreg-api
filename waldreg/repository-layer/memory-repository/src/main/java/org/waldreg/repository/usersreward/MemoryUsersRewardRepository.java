package org.waldreg.repository.usersreward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.dto.UsersRewardTagDto;
import org.wadlreg.reward.users.spi.repository.UsersRewardRepository;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.rewardtag.RewardTagWrapper;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryRewardTagStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryUsersRewardRepository implements UsersRewardRepository{

    private final MemoryRewardTagStorage memoryRewardTagStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final UsersRewardMapper usersRewardMapper;

    @Autowired
    public MemoryUsersRewardRepository(MemoryRewardTagStorage memoryRewardTagStorage,
            MemoryUserStorage memoryUserStorage,
            UsersRewardMapper usersRewardMapper){
        this.memoryRewardTagStorage = memoryRewardTagStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.usersRewardMapper = usersRewardMapper;
    }

    @Override
    public void assignRewardToUser(int id, int rewardTagId){
        RewardTag rewardTag = memoryRewardTagStorage.readRewardTag(rewardTagId);
        memoryUserStorage.updateUsersRewardTag(id, RewardTagWrapper.builder().rewardTag(rewardTag).build());
    }

    @Override
    public UsersRewardDto readSpecifyUsersReward(int id){
        User user = memoryUserStorage.readUserById(id);
        return usersRewardMapper.userToUsersRewardDto(user);
    }

    @Override
    public void deleteRewardToUser(int id, int rewardId){
        memoryUserStorage.deleteRewardToUser(id, rewardId);
    }

    @Override
    public void resetAllUsersReward(){

    }

    @Override
    public boolean isRewardIdExist(int id, int rewardId){
        return false;
    }

}
