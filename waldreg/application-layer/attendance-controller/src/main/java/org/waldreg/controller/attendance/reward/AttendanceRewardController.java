package org.waldreg.controller.attendance.reward;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.attendance.reward.AttendanceRewardManager;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class AttendanceRewardController{

    private final AttendanceRewardManager attendanceRewardManager;

    @Autowired
    public AttendanceRewardController(AttendanceRewardManager attendanceRewardManager){
        this.attendanceRewardManager = attendanceRewardManager;
    }

    @Authenticating
    @PermissionVerifying("Attendance manager")
    @GetMapping("/attendance/reward-tag")
    public void setRewardTagToAttendanceType(@PathVariable("attendance-type") String attendanceType,
                                                @PathVariable("reward-tag-id") int rewardTagId){
        attendanceRewardManager.setRewardTag(rewardTagId, AttendanceType.getAttendanceType(attendanceType));
    }

}
