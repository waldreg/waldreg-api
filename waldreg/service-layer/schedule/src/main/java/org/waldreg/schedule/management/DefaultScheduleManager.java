package org.waldreg.schedule.management;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.InvalidSchedulePeriodException;
import org.waldreg.schedule.spi.ScheduleRepository;

@Service
public class DefaultScheduleManager implements ScheduleManager{

    private final ScheduleRepository scheduleRepository;

    public DefaultScheduleManager(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void createSchedule(ScheduleDto scheduleDto){
        try{
            LocalDateTime startedAt = LocalDateTime.parse(scheduleDto.getStartedAt());
            LocalDateTime finishAt = LocalDateTime.parse(scheduleDto.getFinishAt());
            throwIfDateFormatException(startedAt, finishAt);
            throwIfContentOverflowException(scheduleDto.getScheduleContent());
            throwIfInvalidSchedulePeriodException(startedAt, finishAt);
            throwIfRepeatExist(startedAt, finishAt, scheduleDto.getRepeatDto());
            scheduleRepository.createSchedule(scheduleDto);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException("Invalid date format detected : Schedule start date \"" + scheduleDto.getStartedAt() + "\" Schedule finish date \"" + scheduleDto.getFinishAt() + "\"");
        }
    }

    private void throwIfDateFormatException(LocalDateTime startedAt, LocalDateTime finishAt){
        throwIfUnderYearLimit(startedAt.getYear(), finishAt.getYear());
    }

    private void throwIfUnderYearLimit(int startedYear, int finishYear){
        if (startedYear < 2000 || finishYear < 2000){
            throw new InvalidDateFormatException("year is under 2000");
        }
    }

    private void throwIfContentOverflowException(String content){
        int length = content.length();
        if (length > 1000){
            throw new ContentOverflowException("Schedule length content cannot be more than 1000 : current length \"" + length);
        }
    }

    private void throwIfInvalidSchedulePeriodException(LocalDateTime startedAt, LocalDateTime finishAt){
        if (startedAt.isAfter(finishAt)){
            throw new InvalidSchedulePeriodException("Schedule finish date \"" + finishAt + "\" cannot precede start date \"" + startedAt + "\"");
        }
    }

    private void throwIfRepeatExist(LocalDateTime startedAt, LocalDateTime finishAt, RepeatDto repeatDto){
        if (repeatDtoExist(repeatDto)){
            throwIfInvalidRepeatException(startedAt, finishAt, repeatDto);
        }
    }

    private boolean repeatDtoExist(RepeatDto repeatDto){
        return repeatDto != null;
    }

    private void throwIfInvalidRepeatException(LocalDateTime startedAt, LocalDateTime finishAt, RepeatDto repeatDto){
        try{
            LocalDateTime repeatFinishAt = LocalDateTime.parse(repeatDto.getRepeatFinishAt());
            throwIfCycleIsLessThanOrEqualTpZero(repeatDto.getCycle());
            throwIfInvalidRepeatFinishAt(startedAt, finishAt, repeatFinishAt);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException("Invalid date format detected : Schedule repeat finish date \"" + repeatDto.getRepeatFinishAt() + "\"");
        }
    }

    private void throwIfCycleIsLessThanOrEqualTpZero(int cycle){
        if (cycle <= 0){
            throw new InvalidRepeatException("Cycle cannot be less than or equal to zero, current cycle \"" + cycle + "\"");
        }
    }

    private void throwIfInvalidRepeatFinishAt(LocalDateTime startedAt, LocalDateTime finishAt, LocalDateTime repeatFinishAt){
        if (repeatFinishAt.isBefore(finishAt) || repeatFinishAt.isBefore(startedAt)){
            throw new InvalidRepeatException("Repeat finish date \"" + repeatFinishAt + "\" cannot precede schedule start date \"" + startedAt + "\" or finish date \"" + finishAt + "\"");
        }
    }

    @Override
    public ScheduleDto readScheduleById(int id){
        return scheduleRepository.readScheduleById(id);
    }

    @Override
    public List<ScheduleDto> readScheduleByTerm(int year, int month){
        return scheduleRepository.readScheduleByTerm(year, month);
    }

}
