package org.waldreg.repository.attendance.management.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceDayDto.AttendanceUserInDayDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto.AttendanceUserStatus;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
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

    public List<AttendanceDayDto> attendanceListToAttendanceDayDtoList(List<Attendance> attendanceList){
        return attendanceList.stream()
                .map(a -> AttendanceDayDto.builder()
                            .attendanceDate(a.getAttendanceDate())
                            .attendanceUserList(attendanceList.stream()
                                    .filter(ia -> ia.getAttendanceDate().isEqual(a.getAttendanceDate()))
                                    .map(ia -> AttendanceUserInDayDto
                                            .builder()
                                            .id(ia.getAttendanceUser().getUser().getId())
                                            .userId(ia.getAttendanceUser().getUser().getUserId())
                                            .userName(ia.getAttendanceUser().getUser().getName())
                                            .attendanceStatus(AttendanceType.getAttendanceType(ia.getAttendanceType().getName()))
                                            .build())
                                    .collect(Collectors.toCollection(ArrayList::new)))
                            .build()
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public AttendanceUserDto attendanceListToAttendanceUserDto(List<Attendance> attendanceList){
        return AttendanceUserDto.builder()
                .id(attendanceList.get(0).getAttendanceUser().getUser().getId())
                .userId(attendanceList.get(0).getAttendanceUser().getUser().getUserId())
                .userName(attendanceList.get(0).getAttendanceUser().getUser().getName())
                .attendanceUserStatusList(
                        attendanceList.stream().map(ia -> AttendanceUserStatus.builder()
                                        .attendanceDate(ia.getAttendanceDate())
                                        .attendanceStatus(AttendanceType.getAttendanceType(ia.getAttendanceType().getName()))
                                        .build()
                        ).collect(Collectors.toCollection(ArrayList::new))
                ).build();
    }

}
