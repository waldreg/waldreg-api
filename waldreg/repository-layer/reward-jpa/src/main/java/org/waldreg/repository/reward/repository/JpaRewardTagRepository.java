package org.waldreg.repository.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository("rewardJpaRewardTagRepository")
public interface JpaRewardTagRepository extends JpaRepository<RewardTag, Integer>{
}
