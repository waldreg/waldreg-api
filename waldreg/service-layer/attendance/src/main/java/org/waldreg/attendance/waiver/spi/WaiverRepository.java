package org.waldreg.attendance.waiver.spi;

import java.util.List;
import org.waldreg.attendance.waiver.dto.WaiverDto;

public interface WaiverRepository{

    boolean isAttendanceTarget(int id);

    List<WaiverDto> readWaiverList();

}
