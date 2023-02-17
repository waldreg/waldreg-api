package org.waldreg.controller.attendance.waiver;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.WaiverManager;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.attendance.waiver.mapper.WaiverControllerMapper;
import org.waldreg.controller.attendance.waiver.request.AttendanceWaiverRequest;
import org.waldreg.controller.attendance.waiver.response.AttendanceWaiverResponse;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class WaiverController{

    private final WaiverManager waiverManager;
    private final WaiverControllerMapper waiverControllerMapper;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public WaiverController(WaiverManager waiverManager,
                            WaiverControllerMapper waiverControllerMapper,
                            DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.waiverManager = waiverManager;
        this.waiverControllerMapper = waiverControllerMapper;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @Authenticating
    @PostMapping("/attendance/manager")
    public void waiveAttendance(@RequestBody AttendanceWaiverRequest attendanceWaiverRequest){
        int id = decryptedTokenContextGetter.get();
        WaiverDto waiverDto = waiverControllerMapper.attendanceWaiverRequestToWavierDto(id, attendanceWaiverRequest);
        waiverManager.waive(waiverDto);
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @GetMapping("/attendance/manager")
    public Map<String, List<AttendanceWaiverResponse>> readWaivedMap(){
        List<WaiverDto> waiverDtoList = waiverManager.readWaiverList();
        List<AttendanceWaiverResponse> attendanceWaiverResponseList = waiverControllerMapper.waiverDtoListToAttendanceWaiverResponseList(waiverDtoList);
        return Map.of("waivers", attendanceWaiverResponseList);
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @GetMapping("/attendance/waiver/{waiver-id}/{waiver-type}")
    public void acceptWaiver(@PathVariable("waiver-id") int waiverId, @PathVariable("waiver-type") String waiverType){
        AttendanceType attendanceType = AttendanceType.getAttendanceType(waiverType);
        waiverManager.acceptWaiver(waiverId, attendanceType);
    }

}
