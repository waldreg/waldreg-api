package org.waldreg.attendance.schedule.spi;

import java.time.LocalDate;
import java.util.List;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;

public interface AttendanceScheduleRepository{

    List<AttendanceUserStatusDto> readAttendanceUserStatusList();

    void createNewAttendanceCalendar(LocalDate localDate);

}
