package org.waldreg.repository.attendance.management.mapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceDayDto.AttendanceUserInDayDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto.AttendanceUserStatus;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;

@Component
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
        Map<LocalDate, List<Attendance>> attendanceMap = convertAttendanceListToAttendanceMap(attendanceList);
        List<AttendanceDayDto> attendanceDayDtoList = new ArrayList<>();
        for(Map.Entry<LocalDate, List<Attendance>> entry : attendanceMap.entrySet()){
            AttendanceDayDto attendanceDayDto = AttendanceDayDto.builder()
                    .attendanceDate(entry.getKey())
                    .attendanceUserList(entry.getValue().stream().map(
                            au -> AttendanceUserInDayDto.builder()
                                    .id(au.getAttendanceUser().getUser().getId())
                                    .userId(au.getAttendanceUser().getUser().getUserId())
                                    .userName(au.getAttendanceUser().getUser().getName())
                                    .attendanceStatus(AttendanceType.getAttendanceType(au.getAttendanceType().getName()))
                                    .build()
                    ).collect(Collectors.toList()))
                    .build();
            attendanceDayDtoList.add(attendanceDayDto);
        }
        return attendanceDayDtoList;
    }

    private Map<LocalDate, List<Attendance>> convertAttendanceListToAttendanceMap(List<Attendance> attendanceList){
        Map<LocalDate, List<Attendance>> attendanceMap = new HashMap<>();
        for(Attendance attendance : attendanceList){
            if(!attendanceMap.containsKey(attendance.getAttendanceDate())){
                attendanceMap.put(attendance.getAttendanceDate(), new ArrayList<>());
            }
            attendanceMap.get(attendance.getAttendanceDate()).add(attendance);
        }
        return attendanceMap;
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
