package org.waldreg.controller.attendance.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.attendance.valid.AttendanceValidator;
import org.waldreg.controller.attendance.valid.request.AttendanceIdentifyRequest;
import org.waldreg.token.aop.annotation.Authenticating;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@RestController
public class AttendanceValidController{

    private final AttendanceValidator attendanceValidator;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public AttendanceValidController(AttendanceValidator attendanceValidator,
                                        DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.attendanceValidator = attendanceValidator;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @XssFiltering
    @Authenticating
    @PostMapping("/attendance/confirm")
    public void confirmAttendance(@Xss("json") @RequestBody AttendanceIdentifyRequest attendanceIdentifyRequest){
        int id = decryptedTokenContextGetter.get();
        String identify = attendanceIdentifyRequest.getAttendanceIdentify();
        attendanceValidator.confirm(id, identify);
    }

}
