package org.waldreg.attendance.waiver.spi;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;

public interface WaiverRepository{

    boolean isAttendanceTarget(int id);

    List<WaiverDto> readWaiverList();

    Optional<WaiverDto> readWaiverByWaiverId(int waiverId);

    void acceptWaiver(int id, LocalDate changeDate, AttendanceType attendanceType);

    void deleteWaiver(int waiverId);

}