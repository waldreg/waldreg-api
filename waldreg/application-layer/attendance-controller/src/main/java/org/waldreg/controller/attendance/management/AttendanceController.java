package org.waldreg.controller.attendance.management;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.attendance.management.AttendanceManager;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.attendance.management.mapper.AttendanceControllerMapper;
import org.waldreg.controller.attendance.management.request.AttendanceModifyRequest;
import org.waldreg.controller.attendance.management.response.AttendanceCheckResponse;
import org.waldreg.controller.attendance.management.response.AttendancePerDayResponse;
import org.waldreg.controller.attendance.management.response.AttendanceUserResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class AttendanceController{

    private final AttendanceManager attendanceManager;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;
    private final AttendanceControllerMapper attendanceControllerMapper;

    @Autowired
    public AttendanceController(AttendanceManager attendanceManager,
                                DecryptedTokenContextGetter decryptedTokenContextGetter,
                                AttendanceControllerMapper attendanceControllerMapper){
        this.attendanceManager = attendanceManager;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
        this.attendanceControllerMapper = attendanceControllerMapper;
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @GetMapping("/attendance/subscribed")
    public void registerUserToAttendance(@RequestParam("id") String ids){
        List<Integer> idList = convertIdsToIdList(ids);
        for(Integer id : idList){
            attendanceManager.registerAttendanceTarget(id);
        }
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @DeleteMapping("/attendance/subscribed")
    public void deleteUserToAttendance(@RequestParam("id") String ids){
        List<Integer> idList = convertIdsToIdList(ids);
        for(Integer id : idList){
            attendanceManager.deleteRegisteredAttendanceTarget(id);
        }
    }

    private List<Integer> convertIdsToIdList(String ids){
        return Arrays.stream(ids.split(","))
                .map(i -> Integer.parseInt(i.strip()))
                .collect(Collectors.toList());
    }

    @Authenticating
    @GetMapping("/attendance")
    public AttendanceCheckResponse checkAttendanceRequired(){
        int id = decryptedTokenContextGetter.get();
        AttendanceTargetDto attendanceTargetDto = attendanceManager.readAttendanceTarget(id);
        return attendanceControllerMapper.attendanceTargetDtoToAttendanceCheckResponse(attendanceTargetDto);
    }

    @XssFiltering
    @Authenticating
    @PermissionVerifying("Attendance manager")
    @PostMapping("/attendance/status")
    public void modifyAttendanceStatus(@Xss("json") @RequestBody AttendanceModifyRequest attendanceModifyRequest){
        AttendanceStatusChangeDto attendanceStatusChangeDto = attendanceControllerMapper
                .attendanceModifyRequestToAttendanceChangeDto(attendanceModifyRequest);
        attendanceManager.changeAttendanceStatus(attendanceStatusChangeDto);
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @GetMapping("/attendance/calendar")
    public Map<String, List<AttendancePerDayResponse>> getAttendanceUsersState(@RequestParam("from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
                                                                                @RequestParam("to") @DateTimeFormat(iso = ISO.DATE) LocalDate to){
        List<AttendanceDayDto> attendanceDayDtoList = attendanceManager.readAttendanceStatusList(from, to);
        List<AttendancePerDayResponse> attendancePerDayResponseList = attendanceControllerMapper.attendanceDayDtoListToAttendancePerDayResponseList(attendanceDayDtoList);
        return Map.of("attendances", attendancePerDayResponseList);
    }

    @Authenticating
    @GetMapping("/attendance/calendar/user")
    public AttendanceUserResponse getAttendanceUserResponse(@RequestParam("from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
                                                            @RequestParam("to") @DateTimeFormat(iso = ISO.DATE) LocalDate to){
        int id = decryptedTokenContextGetter.get();
        AttendanceUserDto attendanceUserDto = attendanceManager.readSpecificAttendanceStatusList(id, from, to);
        return attendanceControllerMapper.attendanceUserDtoToAttendanceUserResponse(attendanceUserDto);
    }

}
