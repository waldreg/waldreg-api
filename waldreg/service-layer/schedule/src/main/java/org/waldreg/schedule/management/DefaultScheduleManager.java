package org.waldreg.schedule.management;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Service;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.StartedAtIsAfterFinishAtException;
import org.waldreg.schedule.spi.ScheduleRepository;

@Service
public class DefaultScheduleManager implements ScheduleManager{

    private final ScheduleRepository scheduleRepository;

    public DefaultScheduleManager(ScheduleRepository scheduleRepository){
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void createSchedule(ScheduleDto scheduleDto){
        throwIfDateException(scheduleDto);
        if (repeatDtoExist(scheduleDto)){
            throwIfInvalidRepeatException(scheduleDto);
        }
        scheduleRepository.createSchedule(scheduleDto);
    }

    private void throwIfDateException(ScheduleDto scheduleDto){
        try{
            LocalDateTime startedAt = convertStringToLocalDateTime(scheduleDto.getStartedAt());
            LocalDateTime finishAt = convertStringToLocalDateTime(scheduleDto.getFinishAt());
            throwIfInvalidDateFormatException(startedAt, finishAt);
            throwIfContentOverflowException(scheduleDto.getScheduleContent());
            throwIfStartedAtIsAfterFinishAtException(startedAt, finishAt);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException(DTPE.getMessage());
        }
    }

    private void throwIfInvalidDateFormatException(LocalDateTime startedAt, LocalDateTime finishAt){
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
            throw new ContentOverflowException(Integer.toString(length));
        }
    }

    private void throwIfStartedAtIsAfterFinishAtException(LocalDateTime startedAt, LocalDateTime finishAt){
        if (startedAt.isAfter(finishAt)){
            throw new StartedAtIsAfterFinishAtException();
        }
    }

    private boolean repeatDtoExist(ScheduleDto scheduleDto){
        return scheduleDto.getRepeatDto() != null;
    }

    private void throwIfInvalidRepeatException(ScheduleDto scheduleDto){
        try{
            LocalDateTime startedAt = convertStringToLocalDateTime(scheduleDto.getStartedAt());
            LocalDateTime finishAt = convertStringToLocalDateTime(scheduleDto.getFinishAt());
            LocalDateTime repeatFinishAt = convertStringToLocalDateTime(scheduleDto.getRepeatDto().getRepeatFinishAt());
            throwIfCycleIsZero(scheduleDto.getRepeatDto().getCycle());
            throwIfInvalidRepeatFinishAt(startedAt, finishAt, repeatFinishAt);
        } catch (DateTimeParseException DTPE){
            throw new InvalidDateFormatException(DTPE.getMessage());
        }
    }

    private void throwIfCycleIsZero(int cycle){
        if (cycle == 0){
            throw new InvalidRepeatException("cycle is 0 (valid values > 0)");
        }
    }

    private void throwIfInvalidRepeatFinishAt(LocalDateTime startedAt, LocalDateTime finishAt, LocalDateTime repeatFinishAt){
        if (repeatFinishAt.isBefore(finishAt) || repeatFinishAt.isBefore(startedAt)){
            throw new InvalidRepeatException("repeatFinishAt \"" + repeatFinishAt + "\" is preceding schedule's date");
        }
    }

    private LocalDateTime convertStringToLocalDateTime(String date){
        LocalDateTime localDateTime = LocalDateTime.parse(date);
        return localDateTime;
    }

}
