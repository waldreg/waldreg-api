package org.waldreg.attendance.waiver;

import java.util.List;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;

public interface WaiverManager{

    void waive(WaiverDto waiverRequest);

    List<WaiverDto> readWaiverList();

    void acceptWaiver(int waiverId, AttendanceType attendanceType);

}