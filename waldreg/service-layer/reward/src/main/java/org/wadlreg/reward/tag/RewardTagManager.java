package org.wadlreg.reward.tag;

import org.wadlreg.reward.tag.dto.RewardTagDto;

public interface RewardTagManager{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

}
