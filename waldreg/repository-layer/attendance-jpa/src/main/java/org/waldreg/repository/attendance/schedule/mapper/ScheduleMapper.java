package org.waldreg.repository.attendance.schedule.mapper;

import org.springframework.stereotype.Component;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;

@Component
public final class ScheduleMapper{

    public AttendanceUserStatusDto attendanceToAttendanceUserStatusDto(Attendance attendance){
        return AttendanceUserStatusDto.builder()
                .id(attendance.getAttendanceUser().getUser().getId())
                .attendanceType(AttendanceType.getAttendanceType(attendance.getAttendanceType().getName()))
                .build();
    }

}
