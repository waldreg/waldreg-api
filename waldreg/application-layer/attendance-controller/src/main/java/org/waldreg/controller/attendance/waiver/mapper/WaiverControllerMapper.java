package org.waldreg.controller.attendance.waiver.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.controller.attendance.waiver.request.AttendanceWaiverRequest;
import org.waldreg.controller.attendance.waiver.response.AttendanceWaiverResponse;

@Service
public class WaiverControllerMapper{

    public WaiverDto attendanceWaiverRequestToWavierDto(int id, AttendanceWaiverRequest attendanceWaiverRequest){
        return WaiverDto.builder()
                .id(id)
                .waiverDate(attendanceWaiverRequest.getWaiverDate())
                .waiverReason(attendanceWaiverRequest.getWaiverReason())
                .build();
    }

    public List<AttendanceWaiverResponse> waiverDtoListToAttendanceWaiverResponseList(List<WaiverDto> waiverDtoList){
        return waiverDtoList.stream()
                .map(w -> AttendanceWaiverResponse.builder()
                        .waiverId(w.getWaiverId())
                        .id(w.getId())
                        .userId(w.getUserId())
                        .userName(w.getUserName())
                        .waiverDate(w.getWaiverDate())
                        .waiverReason(w.getWaiverReason())
                        .build()
                ).collect(Collectors.toList());
    }

}
