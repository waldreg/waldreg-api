package org.waldreg.repository.schedule;

import java.time.LocalDateTime;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.domain.calendar.ScheduleRepeat;
import org.waldreg.schedule.dto.ScheduleDto;

public class ScheduleMapper{

    public Schedule scheduleDtoToScheduleDomain(ScheduleDto scheduleDto){
        Schedule.Builder builder = Schedule.builder()
                .scheduleTitle(scheduleDto.getScheduleTitle())
                .scheduleContent(scheduleDto.getScheduleContent())
                .startedAt(LocalDateTime.parse(scheduleDto.getStartedAt()))
                .finishAt(LocalDateTime.parse(scheduleDto.getFinishAt()));
        if (isExistRepeatDto(scheduleDto)){
            builder = builder.scheduleRepeat(ScheduleRepeat.builder()
                    .cycle(scheduleDto.getRepeatDto().getCycle())
                    .repeatFinishAt(LocalDateTime.parse(scheduleDto.getRepeatDto().getRepeatFinishAt()))
                    .build());
        }
        return builder.build();
    }

    private boolean isExistRepeatDto(ScheduleDto scheduleDto){
        return scheduleDto.getRepeatDto() != null;
    }

}
