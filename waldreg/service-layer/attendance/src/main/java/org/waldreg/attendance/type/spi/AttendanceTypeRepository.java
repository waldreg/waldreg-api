package org.waldreg.attendance.type.spi;

public interface AttendanceTypeRepository{

    void createAttendanceTypeIfDoesNotExist(String attendanceType);

}
