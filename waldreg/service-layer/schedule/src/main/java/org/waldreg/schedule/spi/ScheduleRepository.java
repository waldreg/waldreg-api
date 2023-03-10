package org.waldreg.schedule.spi;

import java.util.List;
import org.waldreg.schedule.dto.ScheduleDto;

public interface ScheduleRepository{

    void createSchedule(ScheduleDto scheduleDto);

    ScheduleDto readScheduleById(int id);

    List<ScheduleDto> readScheduleByTerm(int year, int month);

    void updateScheduleById(int id, ScheduleDto scheduleDto);

    void deleteScheduleById(int id);

    boolean isExistScheduleId(int id);

}
