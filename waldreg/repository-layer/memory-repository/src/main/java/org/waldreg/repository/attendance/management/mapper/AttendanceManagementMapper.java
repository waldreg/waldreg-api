package org.waldreg.repository.attendance.management.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;

@Service
public final class AttendanceManagementMapper{

    public AttendanceTargetDto attendanceToAttendanceTargetDto(Attendance attendance){
        return AttendanceTargetDto
                .builder()
                .id(attendance.getAttendanceUser().getUser().getId())
                .userId(attendance.getAttendanceUser().getUser().getUserId())
                .attendanceStatus(AttendanceType.getAttendanceType(attendance.getAttendanceType().getName()))
                .build();
    }

}
