package org.waldreg.attendance.reward;

import org.waldreg.attendance.type.AttendanceType;

public interface AttendanceRewardManager{

    void setRewardTag(int rewardTagId, AttendanceType attendanceType);

}
