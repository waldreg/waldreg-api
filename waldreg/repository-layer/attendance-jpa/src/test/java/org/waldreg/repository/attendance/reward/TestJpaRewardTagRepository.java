package org.waldreg.repository.attendance.reward;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository
public interface TestJpaRewardTagRepository extends JpaRepository<RewardTag, Integer>{
}
