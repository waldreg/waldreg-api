package org.wadlreg.reward.tag.spi;

import java.util.List;
import org.wadlreg.reward.tag.dto.RewardTagDto;

public interface RewardTagRepository{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

    void deleteRewardTag(int rewardTagId);

    List<RewardTagDto> readRewardTagList();

}
