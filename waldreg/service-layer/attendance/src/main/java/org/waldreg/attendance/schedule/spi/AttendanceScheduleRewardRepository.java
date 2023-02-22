package org.waldreg.attendance.schedule.spi;

import org.waldreg.attendance.type.AttendanceType;

public interface AttendanceScheduleRewardRepository{

    void assignRewardToUser(int id, AttendanceType attendanceType);

    boolean isRewardTagPresent(AttendanceType attendanceType);

}
