package org.waldreg.attendance.management.spi;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;

public interface AttendanceRepository{

    void registerAttendanceTarget(int id);

    void deleteRegisteredAttendanceTarget(int id);

    Optional<AttendanceTargetDto> readAttendanceTarget(int id);

    void changeAttendanceStatus(AttendanceStatusChangeDto attendanceStatusChangeDto);

    List<AttendanceDayDto> readAttendanceStatusList(LocalDate from, LocalDate to);

}
