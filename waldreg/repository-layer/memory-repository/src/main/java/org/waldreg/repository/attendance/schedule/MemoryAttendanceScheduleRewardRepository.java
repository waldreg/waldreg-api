package org.waldreg.repository.attendance.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRewardRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.rewardtag.RewardTagWrapper;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public final class MemoryAttendanceScheduleRewardRepository implements AttendanceScheduleRewardRepository{

    private final MemoryUserStorage memoryUserStorage;
    private final MemoryAttendanceStorage memoryAttendanceStorage;

    @Autowired
    public MemoryAttendanceScheduleRewardRepository(MemoryUserStorage memoryUserStorage
                                                    , MemoryAttendanceStorage memoryAttendanceStorage){
        this.memoryUserStorage = memoryUserStorage;
        this.memoryAttendanceStorage = memoryAttendanceStorage;
    }

    @Override
    public void assignRewardToUser(int id, AttendanceType attendanceType){
        AttendanceTypeReward attendanceTypeReward = memoryAttendanceStorage.readAttendanceTypeReward(attendanceType);
        RewardTag rewardTag = attendanceTypeReward.getRewardTag();
        memoryUserStorage.updateUsersRewardTag(id, RewardTagWrapper.builder().rewardTag(rewardTag).build());
    }

    @Override
    public boolean isRewardTagPresent(AttendanceType attendanceType){
        AttendanceTypeReward attendanceTypeReward = memoryAttendanceStorage.readAttendanceTypeReward(attendanceType);
        RewardTag rewardTag = attendanceTypeReward.getRewardTag();
        return rewardTag != null;
    }

}
