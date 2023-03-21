package org.waldreg.repository.reward.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;
import org.waldreg.repository.reward.repository.JpaRewardTagRepository;
import org.waldreg.repository.reward.repository.JpaRewardTagWrapperRepository;
import org.waldreg.repository.reward.repository.JpaRewardUserRepository;
import org.waldreg.repository.reward.user.mapper.RewardTagWrapperMapper;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.reward.users.spi.repository.UsersRewardRepository;

@Repository
public class UsersRewardRepositoryServiceProvider implements UsersRewardRepository{

    private final JpaRewardUserRepository jpaRewardUserRepository;
    private final JpaRewardTagRepository jpaRewardTagRepository;
    private final JpaRewardTagWrapperRepository jpaRewardTagWrapperRepository;
    private final RewardTagWrapperMapper rewardTagWrapperMapper;

    @Autowired
    UsersRewardRepositoryServiceProvider(JpaRewardUserRepository jpaRewardUserRepository,
                                        JpaRewardTagRepository jpaRewardTagRepository,
                                        JpaRewardTagWrapperRepository jpaRewardTagWrapperRepository,
                                        RewardTagWrapperMapper rewardTagWrapperMapper){
        this.jpaRewardUserRepository = jpaRewardUserRepository;
        this.jpaRewardTagRepository = jpaRewardTagRepository;
        this.jpaRewardTagWrapperRepository = jpaRewardTagWrapperRepository;
        this.rewardTagWrapperMapper = rewardTagWrapperMapper;
    }

    @Override
    @Transactional
    public void assignRewardToUser(int id, int rewardTagId){
        User user = getUser(id);
        RewardTag rewardTag = jpaRewardTagRepository.findById(rewardTagId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find rewardTag id \"" + rewardTagId + "\"");}
        );
        user.addRewardTag(rewardTag);
        jpaRewardUserRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public UsersRewardDto readSpecifyUsersReward(int id){
        User user = getUser(id);
        return rewardTagWrapperMapper.userToUsersRewardDto(user);
    }

    @Override
    public List<Integer> readUsersAssignedRewardHistory(){
        return null;
    }

    @Override
    @Transactional
    public void deleteRewardToUser(int id, int rewardId){
        User user = getUser(id);
        user.deleteRewardTag(rewardId);
        jpaRewardUserRepository.flush();
    }

    @Override
    @Transactional
    public void resetAllUsersReward(){
        jpaRewardTagWrapperRepository.deleteAllWrapper();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRewardIdExist(int id, int rewardId){
        User user = getUser(id);
        return user.getRewardTagWrapperList().stream().anyMatch(ur -> ur.getRewardId() == rewardId);
    }

    private User getUser(int id){
        return jpaRewardUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

}
