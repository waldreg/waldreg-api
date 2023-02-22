package org.waldreg.controller.attendance.management.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.controller.attendance.management.request.AttendanceModifyRequest;
import org.waldreg.controller.attendance.management.response.AttendanceCheckResponse;
import org.waldreg.controller.attendance.management.response.AttendancePerDayResponse;
import org.waldreg.controller.attendance.management.response.AttendanceUserResponse;

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

    public AttendanceStatusChangeDto attendanceModifyRequestToAttendanceChangeDto(AttendanceModifyRequest attendanceModifyRequest){
        return AttendanceStatusChangeDto
                .builder()
                .id(attendanceModifyRequest.getId())
                .attendanceType(AttendanceType.getAttendanceType(attendanceModifyRequest.getAttendanceType()))
                .attendanceDate(attendanceModifyRequest.getAttendanceDate())
                .build();
    }

    public List<AttendancePerDayResponse> attendanceDayDtoListToAttendancePerDayResponseList(List<AttendanceDayDto> attendanceDayDtoList){
        return attendanceDayDtoList.stream()
                .map(a -> AttendancePerDayResponse.builder()
                        .attendanceDate(a.getAttendanceDate())
                        .attendanceUserList(a.getAttendanceUserList().stream().map(
                                ia -> AttendancePerDayResponse.AttendanceUserInDayResponse.builder()
                                        .id(ia.getId())
                                        .userId(ia.getUserId())
                                        .userName(ia.getUserName())
                                        .attendanceStatus(ia.getAttendanceStatus())
                                        .build()
                        ).collect(Collectors.toList()))
                        .build()).collect(Collectors.toList());
    }

    public AttendanceUserResponse attendanceUserDtoToAttendanceUserResponse(AttendanceUserDto attendanceUserDto){
        return AttendanceUserResponse.builder()
                .id(attendanceUserDto.getId())
                .userId(attendanceUserDto.getUserId())
                .userName(attendanceUserDto.getUserName())
                .attendanceUserStatusList(attendanceUserDto.getAttendanceUserStatusList().stream()
                        .map(a -> AttendanceUserResponse.AttendanceUserStatus.builder()
                                .attendanceDate(a.getAttendanceDate())
                                .attendanceStatus(a.getAttendanceStatus())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

}
