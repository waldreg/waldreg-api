package org.wadlreg.reward.tag.spi;

import java.util.List;
import org.wadlreg.reward.tag.dto.RewardTagDto;
import org.wadlreg.reward.users.spi.tag.RewardTagExistChecker;

public interface RewardTagRepository extends RewardTagExistChecker{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

    void deleteRewardTag(int rewardTagId);

    List<RewardTagDto> readRewardTagList();

}
