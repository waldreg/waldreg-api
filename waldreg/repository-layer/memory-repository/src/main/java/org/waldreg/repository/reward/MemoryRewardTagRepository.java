package org.waldreg.repository.reward;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.MemoryRewardTagStorage;

@Repository
public class MemoryRewardTagRepository implements RewardTagRepository{

    private final MemoryRewardTagStorage memoryRewardTagStorage;
    private final RewardTagMapper rewardTagMapper;

    @Autowired
    public MemoryRewardTagRepository(MemoryRewardTagStorage memoryRewardTagStorage, RewardTagMapper rewardTagMapper){
        this.memoryRewardTagStorage = memoryRewardTagStorage;
        this.rewardTagMapper = rewardTagMapper;
    }

    @Override
    public void createRewardTag(RewardTagDto rewardTagDto){
        memoryRewardTagStorage.createRewardTag(
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
        List<RewardTag> rewardTagList = memoryRewardTagStorage.readRewardTagList();
        List<RewardTagDto> rewardTagDtoList = new ArrayList<>();
        for(RewardTag rewardTag : rewardTagList){
            rewardTagDtoList.add(rewardTagMapper.rewardTagToRewardTagDto(rewardTag));
        }
        return rewardTagDtoList;
    }

    @Override
    public boolean isRewardTagExist(int rewardTagId){
        return memoryRewardTagStorage.readRewardTag(rewardTagId) != null;
    }

}
