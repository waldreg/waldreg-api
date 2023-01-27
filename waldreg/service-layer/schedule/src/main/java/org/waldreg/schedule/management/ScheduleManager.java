package org.waldreg.schedule.management;

import org.waldreg.schedule.dto.ScheduleDto;

public interface ScheduleManager{

    void createSchedule(ScheduleDto scheduleDto);

    ScheduleDto readScheduleById(int id);

}
