package org.waldreg.schedule.management;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.InvalidSchedulePeriodException;
import org.waldreg.schedule.exception.UnknownScheduleException;
import org.waldreg.schedule.spi.ScheduleRepository;

@Service
public class DefaultScheduleManager implements ScheduleManager{

    private final ScheduleRepository scheduleRepository;

    @Autowired
    public DefaultScheduleManager(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void createSchedule(ScheduleDto scheduleDto){
        try{
            LocalDateTime startedAt = LocalDateTime.parse(scheduleDto.getStartedAt());
            LocalDateTime finishAt = LocalDateTime.parse(scheduleDto.getFinishAt());
            throwIfUnderYearLimit(startedAt.getYear(), finishAt.getYear());
            throwIfContentOverflow(scheduleDto.getScheduleContent());
            throwIfTitleOverflow(scheduleDto.getScheduleTitle());
            throwIfFinishAtPrecedeStartedAt(startedAt, finishAt);
            throwIfRepeatExist(startedAt, finishAt, scheduleDto.getRepeatDto());
            scheduleRepository.createSchedule(scheduleDto);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException("SCHEDULE-403", "Invalid date format detected Schedule start date \"" + scheduleDto.getStartedAt() + "\" Schedule finish date \"" + scheduleDto.getFinishAt() + "\"");
        }
    }

    @Override
    public ScheduleDto readScheduleById(int id){
        throwIfScheduleIdDoesNotExist(id);
        return scheduleRepository.readScheduleById(id);
    }

    @Override
    public List<ScheduleDto> readScheduleByTerm(int year, int month){
        throwIfInvalidDateFormat(year, month);
        return scheduleRepository.readScheduleByTerm(year, month);
    }

    private void throwIfInvalidDateFormat(int year, int month){
        throwIfInvalidYear(year);
        throwIfInvalidMonth(month);
    }

    private void throwIfInvalidYear(int year){
        if (year < 2000){
            throw new InvalidDateFormatException("SCHEDULE-400", "Year cannot be under 2000 current year \"" + year + "\"");
        }
    }

    private void throwIfInvalidMonth(int month){
        if (month < 1 || month > 12){
            throw new InvalidDateFormatException("SCHEDULE-401", "Month cannot be under 1 or over 12 current month \"" + month + "\"");
        }
    }

    @Override
    public void updateScheduleById(int id, ScheduleDto scheduleDto){
        throwIfScheduleIdDoesNotExist(id);
        try{
            LocalDateTime startedAt = LocalDateTime.parse(scheduleDto.getStartedAt());
            LocalDateTime finishAt = LocalDateTime.parse(scheduleDto.getFinishAt());
            throwIfUnderYearLimit(startedAt.getYear(), finishAt.getYear());
            throwIfContentOverflow(scheduleDto.getScheduleContent());
            throwIfTitleOverflow(scheduleDto.getScheduleTitle());
            throwIfFinishAtPrecedeStartedAt(startedAt, finishAt);
            throwIfRepeatExist(startedAt, finishAt, scheduleDto.getRepeatDto());
            scheduleRepository.updateScheduleById(id, scheduleDto);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException("SCHEDULE-403", "Invalid date format detected Schedule start date \"" + scheduleDto.getStartedAt() + "\" Schedule finish date \"" + scheduleDto.getFinishAt() + "\"");
        }
    }

    private void throwIfUnderYearLimit(int startedYear, int finishYear){
        if (startedYear < 2000 || finishYear < 2000){
            throw new InvalidDateFormatException("SCHEDULE-404", "Year cannot be under 2000 current Schedule start year \"" + startedYear + "\" finish year \"" + finishYear + "\"");
        }
    }

    private void throwIfContentOverflow(String content){
        int length = content.length();
        if (length > 1000){
            throw new ContentOverflowException("SCHEDULE-409", "Schedule content length cannot be more than 1000 current length \"" + length + "\"");
        }
    }

    private void throwIfTitleOverflow(String title){
        int length = title.length();
        if (length > 1000){
            throw new ContentOverflowException("SCHEDULE-411", "Schedule title length cannot be more than 1000 current length \"" + length + "\"");
        }
    }

    private void throwIfFinishAtPrecedeStartedAt(LocalDateTime startedAt, LocalDateTime finishAt){
        if (startedAt.isAfter(finishAt)){
            throw new InvalidSchedulePeriodException("SCHEDULE-408", "Schedule finish date \"" + finishAt + "\" cannot precede start date \"" + startedAt + "\"");
        }
    }

    private void throwIfRepeatExist(LocalDateTime startedAt, LocalDateTime finishAt, RepeatDto repeatDto){
        if (isExistRepeatDto(repeatDto)){
            throwIfInvalidRepeat(startedAt, finishAt, repeatDto);
        }
    }

    private boolean isExistRepeatDto(RepeatDto repeatDto){
        return repeatDto != null;
    }

    private void throwIfInvalidRepeat(LocalDateTime startedAt, LocalDateTime finishAt, RepeatDto repeatDto){
        try{
            LocalDateTime repeatFinishAt = LocalDateTime.parse(repeatDto.getRepeatFinishAt());
            throwIfCycleIsLessThanOrEqualToZero(repeatDto.getCycle());
            throwIfRepeatFinishAtPrecedeSchedulePeriod(startedAt, finishAt, repeatFinishAt);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException("SCHEDULE-403", "Invalid date format detected Schedule repeat finish date \"" + repeatDto.getRepeatFinishAt() + "\"");
        }
    }

    private void throwIfCycleIsLessThanOrEqualToZero(int cycle){
        if (cycle <= 0){
            throw new InvalidRepeatException("SCHEDULE-405", "Cycle cannot be less than or equal to zero, current cycle \"" + cycle + "\"");
        }
    }

    private void throwIfRepeatFinishAtPrecedeSchedulePeriod(LocalDateTime startedAt, LocalDateTime finishAt, LocalDateTime repeatFinishAt){
        if (repeatFinishAt.isBefore(finishAt) || repeatFinishAt.isBefore(startedAt)){
            throw new InvalidRepeatException("SCHEDULE-406", "Repeat finish date \"" + repeatFinishAt + "\" cannot precede schedule start date \"" + startedAt + "\" or finish date \"" + finishAt + "\"");
        }
    }

    @Override
    public void deleteScheduleById(int id){
        throwIfScheduleIdDoesNotExist(id);
        scheduleRepository.deleteScheduleById(id);
    }

    private void throwIfScheduleIdDoesNotExist(int id){
        if (!scheduleRepository.isExistScheduleId(id)){
            throw new UnknownScheduleException("SCHEDULE-410", "Cannot find schedule with id \"" + id + "\"");
        }
    }

}
