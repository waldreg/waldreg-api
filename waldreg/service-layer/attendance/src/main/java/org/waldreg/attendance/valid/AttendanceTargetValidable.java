package org.waldreg.attendance.valid;

public interface AttendanceTargetValidable{

    void throwIfDoesNotAttendanceTarget(int id);

    void throwIfDoesNotNeedAttendance(int id);

}
