package org.waldreg.attendance.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class DefaultAttendanceValidator implements AttendanceValidator{

    private final AttendanceIdentifyValidable attendanceIdentifyValidable;
    private final AttendanceTargetValidable attendanceTargetValidable;

    @Autowired
    public DefaultAttendanceValidator(AttendanceIdentifyValidable attendanceIdentifyValidable,
                                        AttendanceTargetValidable attendanceTargetValidable){
        this.attendanceIdentifyValidable = attendanceIdentifyValidable;
        this.attendanceTargetValidable = attendanceTargetValidable;
    }

    @Override
    public void confirm(int id, String attendanceIdentify){
        attendanceTargetValidable.throwIfDoesNotAttendanceTarget(id);
        attendanceTargetValidable.throwIfDoesNotNeedAttendance(id);
        attendanceIdentifyValidable.valid(attendanceIdentify);
    }

}
