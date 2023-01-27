package org.waldreg.schedule.management;

import java.util.List;
import org.waldreg.schedule.dto.ScheduleDto;

public interface ScheduleManager{

    void createSchedule(ScheduleDto scheduleDto);

    ScheduleDto readScheduleById(int id);

    List<ScheduleDto> readScheduleByTerm(int year, int month);

    void updateScheduleById(int id, ScheduleDto scheduleDto);

}
