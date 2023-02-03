package org.waldreg.controller.schedule.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.controller.schedule.request.ScheduleRequest;
import org.waldreg.controller.schedule.response.ScheduleListResponse;
import org.waldreg.controller.schedule.response.ScheduleRepeatResponse;
import org.waldreg.controller.schedule.response.ScheduleResponse;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;

@Service
public class ControllerScheduleMapper{

    public ScheduleDto scheduleRequestToScheduleDto(ScheduleRequest scheduleRequest){
        ScheduleDto.Builder builder = ScheduleDto.builder()
                .scheduleTitle(scheduleRequest.getScheduleTitle())
                .scheduleContent(scheduleRequest.getScheduleContent())
                .startedAt(scheduleRequest.getStartedAt())
                .finishAt(scheduleRequest.getFinishAt());
        if (isExistScheduleRepeatRequest(scheduleRequest)){
            return scheduleRepeatRequestToRepeatDto(scheduleRequest, builder);
        }
        return builder.build();
    }

    public boolean isExistScheduleRepeatRequest(ScheduleRequest scheduleRequest){
        return scheduleRequest.getRepeat() != null;
    }

    public ScheduleDto scheduleRepeatRequestToRepeatDto(ScheduleRequest scheduleRequest, ScheduleDto.Builder builder){
        return builder.repeatDto(RepeatDto.builder()
                        .cycle(scheduleRequest.getRepeat().getCycle())
                        .repeatFinishAt(scheduleRequest.getRepeat().getRepeatFinishAt())
                        .build())
                .build();
    }

    public ScheduleListResponse scheduleDtoListToScheduleListResponse(List<ScheduleDto> scheduleDtoList){
        List<ScheduleResponse> scheduleResponseList = new ArrayList<>();
        for(ScheduleDto scheduleDto : scheduleDtoList){
            scheduleResponseList.add(scheduleDtoToScheduleResponse(scheduleDto));
        }
        return ScheduleListResponse.builder()
                .scheduleList(scheduleResponseList)
                .build();
    }

    public ScheduleResponse scheduleDtoToScheduleResponse(ScheduleDto scheduleDto){
        ScheduleResponse.Builder builder = ScheduleResponse.builder()
                .id(scheduleDto.getId())
                .scheduleTitle(scheduleDto.getScheduleTitle())
                .scheduleContent(scheduleDto.getScheduleContent())
                .startedAt(scheduleDto.getStartedAt())
                .finishAt(scheduleDto.getFinishAt());
        if (isExistScheduleRepeatDto(scheduleDto)){
            return repeatDtoToScheduleRepeatResponse(scheduleDto, builder);
        }
        return builder.build();
    }

    public boolean isExistScheduleRepeatDto(ScheduleDto scheduleDto){
        return scheduleDto.getRepeatDto() != null;
    }

    public ScheduleResponse repeatDtoToScheduleRepeatResponse(ScheduleDto scheduleDto, ScheduleResponse.Builder builder){
        return builder.repeat(ScheduleRepeatResponse.builder()
                        .cycle(scheduleDto.getRepeatDto().getCycle())
                        .repeatFinishAt(scheduleDto.getRepeatDto().getRepeatFinishAt())
                        .build())
                .build();
    }

}
