package org.waldreg.attendance.waiver;

import org.waldreg.attendance.waiver.dto.WaiverDto;

public interface WaiverManager{

    void waive(WaiverDto waiverRequest);

}