package org.waldreg.attendance.management;

import java.time.LocalDate;
import java.util.List;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.type.AttendanceType;

public interface AttendanceManager{

    void registerAttendanceTarget(int id);

    void deleteRegisteredAttendanceTarget(int id);

    AttendanceTargetDto readAttendanceTarget(int id);

    void changeAttendanceStatus(AttendanceStatusChangeDto attendanceStatusChangeDto);

    List<AttendanceDayDto> readAttendanceStatusList(LocalDate from, LocalDate to);

    AttendanceUserDto readSpecificAttendanceStatusList(int id, LocalDate from, LocalDate to);

    void setRewardTag(int rewardTagId, AttendanceType attendanceType);

}
