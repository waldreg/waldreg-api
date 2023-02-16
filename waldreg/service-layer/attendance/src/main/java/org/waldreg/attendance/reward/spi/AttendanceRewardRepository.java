package org.waldreg.attendance.reward.spi;

import org.waldreg.attendance.type.AttendanceType;

public interface AttendanceRewardRepository{

    boolean isRewardTagExist(int rewardTagId);

    void setRewardTag(int rewardTagId, AttendanceType attendanceType);

}
