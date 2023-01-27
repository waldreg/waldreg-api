package org.waldreg.schedule.spi;

import org.waldreg.schedule.dto.ScheduleDto;

public interface ScheduleRepository{

    void createSchedule(ScheduleDto scheduleDto);

}
