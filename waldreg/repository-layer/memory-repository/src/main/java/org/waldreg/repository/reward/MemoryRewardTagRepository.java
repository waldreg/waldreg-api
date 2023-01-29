package org.waldreg.repository.reward;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;
import org.waldreg.repository.RewardTagStorage;

@Repository
public class MemoryRewardTagRepository implements RewardTagRepository{

    private final RewardTagStorage rewardTagStorage;
    private final RewardTagMapper rewardTagMapper;

    @Autowired
    public MemoryRewardTagRepository(RewardTagStorage rewardTagStorage, RewardTagMapper rewardTagMapper){
        this.rewardTagStorage = rewardTagStorage;
        this.rewardTagMapper = rewardTagMapper;
    }

    @Override
    public void createRewardTag(RewardTagDto rewardTagDto){
        rewardTagStorage.createRewardTag(
                rewardTagMapper.rewardTagDtoToRewardTag(rewardTagDto)
        );
    }

    @Override
    public void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto){

    }

    @Override
    public void deleteRewardTag(int rewardTagId){

    }

    @Override
    public List<RewardTagDto> readRewardTagList(){
        return null;
    }

    @Override
    public boolean isRewardTagExist(int rewardTagId){
        return false;
    }

}
