package org.waldreg.reward.tag.spi;

import java.util.List;
import org.waldreg.reward.tag.dto.RewardTagDto;
import org.waldreg.reward.users.spi.tag.RewardTagExistChecker;

public interface RewardTagRepository extends RewardTagExistChecker{

    void createRewardTag(RewardTagDto rewardTagDto);

    void updateRewardTag(int rewardTagId, RewardTagDto rewardTagDto);

    void deleteRewardTag(int rewardTagId);

    List<RewardTagDto> readRewardTagList();

}
