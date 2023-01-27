package org.wadlreg.reward.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.tag.spi.RewardTagRepository;

@Service
public class DefaultRewardTagManager implements RewardTagManager{

    private final RewardTagRepository rewardTagRepository;

    @Autowired
    public DefaultRewardTagManager(RewardTagRepository rewardTagRepository){
        this.rewardTagRepository = rewardTagRepository;
    }

    @Override
    public void createRewardTag(RewardTagDto rewardTagDto){
        rewardTagRepository.createRewardTag(rewardTagDto);
    }

}
