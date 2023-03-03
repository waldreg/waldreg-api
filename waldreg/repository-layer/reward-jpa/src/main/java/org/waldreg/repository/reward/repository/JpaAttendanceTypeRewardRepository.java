package org.waldreg.repository.reward.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.rewardtag.RewardTag;

@Repository("rewardJpaAttendanceTypeRewardRepository")
public interface JpaAttendanceTypeRewardRepository extends JpaRepository<AttendanceTypeReward, Integer>{

    List<AttendanceTypeReward> findByRewardTag(RewardTag rewardTag);

}
