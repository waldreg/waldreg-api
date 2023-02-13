package org.waldreg.attendance.management.spi;

public interface AttendanceRepository{

    void registerAttendanceTarget(int id);

    void deleteRegisteredAttendanceTarget(int id);

}
