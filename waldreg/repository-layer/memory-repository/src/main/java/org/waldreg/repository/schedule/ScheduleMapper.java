package org.waldreg.repository.schedule;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.domain.calendar.ScheduleRepeat;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;

@Service
public class ScheduleMapper{

    public Schedule scheduleDtoToScheduleDomain(ScheduleDto scheduleDto){
        Schedule.Builder builder = Schedule.builder()
                .scheduleTitle(scheduleDto.getScheduleTitle())
                .scheduleContent(scheduleDto.getScheduleContent())
                .startedAt(LocalDateTime.parse(scheduleDto.getStartedAt()))
                .finishAt(LocalDateTime.parse(scheduleDto.getFinishAt()));
        if (isExistRepeatDto(scheduleDto)){
            return repeatDtoToScheduleRepeat(scheduleDto, builder);
        }
        return builder.build();
    }

    private boolean isExistRepeatDto(ScheduleDto scheduleDto){
        return scheduleDto.getRepeatDto() != null;
    }

    private Schedule repeatDtoToScheduleRepeat(ScheduleDto scheduleDto, Schedule.Builder builder){
        return builder.scheduleRepeat(ScheduleRepeat.builder()
                        .cycle(scheduleDto.getRepeatDto().getCycle())
                        .repeatFinishAt(LocalDateTime.parse(scheduleDto.getRepeatDto().getRepeatFinishAt()))
                        .build())
                .build();
    }

    public ScheduleDto scheduleDomainToScheduleDto(Schedule schedule){
        ScheduleDto.Builder builder = ScheduleDto.builder()
                .id(schedule.getId())
                .scheduleTitle(schedule.getScheduleTitle())
                .scheduleContent(schedule.getScheduleContent())
                .startedAt(schedule.getStartedAt().toString())
                .finishAt(schedule.getFinishAt().toString());
        if (isExistScheduleRepeat(schedule)){
            return scheduleRepeatToRepeatDto(schedule, builder);
        }
        return builder.build();
    }

    private ScheduleDto scheduleRepeatToRepeatDto(Schedule schedule, ScheduleDto.Builder builder){
        return builder.repeatDto(RepeatDto.builder()
                        .cycle(schedule.getScheduleRepeat().getCycle())
                        .repeatFinishAt(schedule.getScheduleRepeat().getRepeatFinishAt().toString())
                        .build())
                .build();
    }

    private boolean isExistScheduleRepeat(Schedule schedule){
        return schedule.getScheduleRepeat() != null;
    }

}
