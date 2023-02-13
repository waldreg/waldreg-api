package org.waldreg.attendance.management;

import org.waldreg.attendance.management.dto.AttendanceTargetDto;

public interface AttendanceManager{

    void registerAttendanceTarget(int id);

    void deleteRegisteredAttendanceTarget(int id);

    AttendanceTargetDto getAttendanceTarget(int id);

}
