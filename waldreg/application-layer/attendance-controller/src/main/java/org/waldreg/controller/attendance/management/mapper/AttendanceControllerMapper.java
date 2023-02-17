package org.waldreg.controller.attendance.management.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.controller.attendance.management.response.AttendanceCheckResponse;

@Service
public class AttendanceControllerMapper{

    public AttendanceCheckResponse attendanceTargetDtoToAttendanceCheckResponse(AttendanceTargetDto attendanceTargetDto){
        return AttendanceCheckResponse.builder()
                .id(attendanceTargetDto.getId())
                .userId(attendanceTargetDto.getUserId())
                .attendanceRequired(attendanceTargetDto.getAttendanceStatus().isAttendanceRequire())
                .attendanceStatus(attendanceTargetDto.getAttendanceStatus())
                .build();
    }

}
