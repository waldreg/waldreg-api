package org.waldreg.repository.schedule;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.repository.MemoryScheduleStorage;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.spi.repository.ScheduleRepository;

@Repository
public class MemoryScheduleRepository implements ScheduleRepository{

    private final MemoryScheduleStorage memoryScheduleStorage;
    private final ScheduleMapper scheduleMapper;

    public MemoryScheduleRepository(MemoryScheduleStorage memoryScheduleStorage, ScheduleMapper scheduleMapper){
        this.memoryScheduleStorage = memoryScheduleStorage;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    public void createSchedule(ScheduleDto scheduleDto){
        Schedule schedule = scheduleMapper.scheduleDtoToScheduleDomain(scheduleDto);
        memoryScheduleStorage.createSchedule(schedule);
    }

    @Override
    public ScheduleDto readScheduleById(int id){
        return null;
    }

    @Override
    public List<ScheduleDto> readScheduleByTerm(int year, int month){
        return null;
    }

    @Override
    public void updateScheduleById(int id, ScheduleDto scheduleDto){

    }

    @Override
    public void deleteScheduleById(int id){

    }

}
