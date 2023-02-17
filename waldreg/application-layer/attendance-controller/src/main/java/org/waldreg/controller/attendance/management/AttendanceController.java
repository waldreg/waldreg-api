package org.waldreg.controller.attendance.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.attendance.management.AttendanceManager;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.attendance.management.mapper.AttendanceControllerMapper;
import org.waldreg.controller.attendance.management.response.AttendanceCheckResponse;
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
        return Arrays.stream(ids.split(" "))
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

}
