package org.waldreg.controller.attendance.waiver.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.controller.attendance.waiver.request.AttendanceWaiverRequest;

@Service
public class WaiverControllerMapper{

    public WaiverDto attendanceWaiverRequestToWavierDto(AttendanceWaiverRequest attendanceWaiverRequest){
        return WaiverDto.builder()
                .waiverDate(attendanceWaiverRequest.getWaiverDate())
                .waiverReason(attendanceWaiverRequest.getWaiverReason())
                .build();
    }

}
