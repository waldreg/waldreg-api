package org.waldreg.reward.tag;

import java.util.List;
import org.waldreg.reward.tag.dto.RewardTagDto;

public interface RewardTagManager{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

    void deleteRewardTag(int rewardTagId);

    List<RewardTagDto> readRewardTagList();

}
