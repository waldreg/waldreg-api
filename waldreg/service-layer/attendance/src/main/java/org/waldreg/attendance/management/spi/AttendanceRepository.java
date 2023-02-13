package org.waldreg.attendance.management.spi;

import java.util.Optional;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;

public interface AttendanceRepository{

    void registerAttendanceTarget(int id);

    void deleteRegisteredAttendanceTarget(int id);

    Optional<AttendanceTargetDto> getAttendanceTarget(int id);

}
