package org.waldreg.attendance.valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.valid.spi.AttendanceValidatorRepository;

@Service
public final class DefaultAttendanceValidator implements AttendanceValidator{

    private final AttendanceIdentifyValidable attendanceIdentifyValidable;
    private final AttendanceTargetValidable attendanceTargetValidable;
    private final AttendanceValidatorRepository attendanceValidatorRepository;

    @Autowired
    public DefaultAttendanceValidator(AttendanceIdentifyValidable attendanceIdentifyValidable,
                                        AttendanceTargetValidable attendanceTargetValidable,
                                        AttendanceValidatorRepository attendanceValidatorRepository){
        this.attendanceIdentifyValidable = attendanceIdentifyValidable;
        this.attendanceTargetValidable = attendanceTargetValidable;
        this.attendanceValidatorRepository = attendanceValidatorRepository;
    }

    @Override
    public void confirm(int id, String attendanceIdentify){
        attendanceTargetValidable.throwIfDoesNotAttendanceTarget(id);
        attendanceTargetValidable.throwIfDoesNotNeedAttendance(id);
        attendanceIdentifyValidable.valid(attendanceIdentify);
        attendanceValidatorRepository.confirm(id);
    }

}
