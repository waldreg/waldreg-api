package org.waldreg.controller.schedule.mapper;

import org.waldreg.controller.schedule.request.ScheduleRequest;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;

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

}
