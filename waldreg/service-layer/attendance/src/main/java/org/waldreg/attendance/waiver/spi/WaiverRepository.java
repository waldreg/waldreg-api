package org.waldreg.attendance.waiver.spi;

import java.util.List;
import java.util.Optional;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;

public interface WaiverRepository{

    void waive(WaiverDto waiverRequest);

    boolean isAttendanceTarget(int id);

    List<WaiverDto> readWaiverList();

    Optional<WaiverDto> readWaiverByWaiverId(int waiverId);

    void acceptWaiver(int waiverId, AttendanceType attendanceType);

    void deleteWaiver(int waiverId);

}
