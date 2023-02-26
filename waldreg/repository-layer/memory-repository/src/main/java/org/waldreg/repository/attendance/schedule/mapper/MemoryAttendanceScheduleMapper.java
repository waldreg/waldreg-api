package org.waldreg.repository.attendance.schedule.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;

@Service
public class MemoryAttendanceScheduleMapper{

    public List<AttendanceUserStatusDto> attendanceListToAttendanceUserStatusDtoList(List<Attendance> attendanceList){
        return attendanceList.stream()
                .map(a -> AttendanceUserStatusDto.builder()
                        .id(a.getAttendanceUser().getUser().getId())
                        .attendanceType(AttendanceType.getAttendanceType(a.getAttendanceType().getName()))
                        .build())
                .collect(Collectors.toList());
    }

}
