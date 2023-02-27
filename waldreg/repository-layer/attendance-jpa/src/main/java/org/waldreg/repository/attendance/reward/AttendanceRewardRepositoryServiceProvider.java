package org.waldreg.repository.attendance.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.reward.spi.AttendanceRewardRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.reward.repository.JpaAttendanceRewardTagRepository;

@Repository
public class AttendanceRewardRepositoryServiceProvider implements AttendanceRewardRepository{

    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;
    private final JpaAttendanceRewardTagRepository jpaAttendanceRewardTagRepository;

    @Autowired
    AttendanceRewardRepositoryServiceProvider(JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository,
            JpaAttendanceRewardTagRepository jpaAttendanceRewardTagRepository){
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
        this.jpaAttendanceRewardTagRepository = jpaAttendanceRewardTagRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRewardTagExist(int rewardTagId){
        return jpaAttendanceRewardTagRepository.existsById(rewardTagId);
    }

    @Override
    @Transactional
    public void setRewardTag(int rewardTagId, AttendanceType attendanceType){
        RewardTag rewardTag = jpaAttendanceRewardTagRepository.findById(rewardTagId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find RewardTag id \"" + rewardTagId + "\"");}
        );
        AttendanceTypeReward attendanceTypeReward = getAttendanceTypeReward(attendanceType);
        attendanceTypeReward.setRewardTag(rewardTag);
    }

    private AttendanceTypeReward getAttendanceTypeReward(AttendanceType attendanceType){
        return jpaAttendanceTypeRewardRepository.findByName(attendanceType.toString())
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find AttendanceTypeReward named \"" + attendanceType + "\"");});
    }

}
