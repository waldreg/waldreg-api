package org.waldreg.repository.attendance.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.reward.spi.AttendanceRewardRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryRewardTagStorage;

@Repository
public final class MemoryAttendanceRewardRepository implements AttendanceRewardRepository{

    private final MemoryRewardTagStorage memoryRewardTagStorage;
    private final MemoryAttendanceStorage memoryAttendanceStorage;

    @Autowired
    public MemoryAttendanceRewardRepository(MemoryRewardTagStorage memoryRewardTagStorage,
                                            MemoryAttendanceStorage memoryAttendanceStorage){
        this.memoryRewardTagStorage = memoryRewardTagStorage;
        this.memoryAttendanceStorage = memoryAttendanceStorage;
    }

    @Override
    public boolean isRewardTagExist(int rewardTagId){
        return memoryRewardTagStorage.readRewardTag(rewardTagId) != null;
    }

    @Override
    public void setRewardTag(int rewardTagId, AttendanceType attendanceType){
        RewardTag rewardTag = memoryRewardTagStorage.readRewardTag(rewardTagId);
        memoryAttendanceStorage.setRewardTagToAttendanceType(attendanceType, rewardTag);
    }

}
