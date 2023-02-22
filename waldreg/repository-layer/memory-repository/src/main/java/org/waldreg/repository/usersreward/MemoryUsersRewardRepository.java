package org.waldreg.repository.usersreward;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.reward.users.spi.repository.UserExistChecker;
import org.waldreg.reward.users.spi.repository.UsersRewardRepository;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.rewardtag.RewardTagWrapper;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryRewardTagStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryUsersRewardRepository implements UsersRewardRepository, UserExistChecker{

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
        memoryUserStorage.deleteAllUsersReward();
    }

    @Override
    public boolean isRewardIdExist(int id, int rewardId){
        User user = memoryUserStorage.readUserById(id);
        return isUserHasRewardId(user, rewardId);
    }

    private boolean isUserHasRewardId(User user, int rewardId){
        List<RewardTagWrapper> rewardTagWrapperList = user.getRewardTagWrapperList();
        return rewardTagWrapperList.stream().anyMatch(rewardTagWrapper -> rewardTagWrapper.getRewardId() == rewardId);
    }

    @Override
    public boolean isUserExist(int id){
        try{
            memoryUserStorage.readUserById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }

}
