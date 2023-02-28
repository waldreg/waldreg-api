package org.waldreg.repository.attendance.reward.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository
public interface JpaAttendanceRewardTagRepository extends JpaRepository<RewardTag, Integer>{

}
