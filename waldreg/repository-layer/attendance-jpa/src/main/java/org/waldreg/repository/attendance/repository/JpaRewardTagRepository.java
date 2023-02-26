package org.waldreg.repository.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository
public interface JpaRewardTagRepository extends JpaRepository<RewardTag, Integer>{
}
