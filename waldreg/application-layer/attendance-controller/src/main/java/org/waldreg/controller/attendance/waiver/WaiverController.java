package org.waldreg.controller.attendance.waiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.attendance.waiver.WaiverManager;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.controller.attendance.waiver.mapper.WaiverControllerMapper;
import org.waldreg.controller.attendance.waiver.request.AttendanceWaiverRequest;

@RestController
public class WaiverController{

    private final WaiverManager waiverManager;
    private final WaiverControllerMapper waiverControllerMapper;

    @Autowired
    public WaiverController(WaiverManager waiverManager,
                            WaiverControllerMapper waiverControllerMapper){
        this.waiverManager = waiverManager;
        this.waiverControllerMapper = waiverControllerMapper;
    }

    @PostMapping("/attendance/manager")
    public void waiveAttendance(@RequestBody AttendanceWaiverRequest attendanceWaiverRequest){
        WaiverDto waiverDto = waiverControllerMapper.attendanceWaiverRequestToWavierDto(attendanceWaiverRequest);
        waiverManager.waive(waiverDto);
    }

}
