package org.wadlreg.reward.tag.spi;

import org.wadlreg.reward.tag.dto.RewardTagDto;

public interface RewardTagRepository{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

    void deleteRewardTag(int rewardTagId);

}
