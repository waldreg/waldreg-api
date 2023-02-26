package org.waldreg.repository.schedule;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.repository.MemoryScheduleStorage;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.spi.ScheduleRepository;

@Repository
public class MemoryScheduleRepository implements ScheduleRepository{

    private final MemoryScheduleStorage memoryScheduleStorage;
    private final ScheduleMapper scheduleMapper;

    @Autowired
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
        Schedule schedule = memoryScheduleStorage.readScheduleById(id);
        return scheduleMapper.scheduleDomainToScheduleDto(schedule);
    }

    @Override
    public List<ScheduleDto> readScheduleByTerm(int year, int month){
        List<Schedule> scheduleList = memoryScheduleStorage.readScheduleByTerm(year, month);
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        for (Schedule schedule : scheduleList){
            scheduleDtoList.add(scheduleMapper.scheduleDomainToScheduleDto(schedule));
        }
        return scheduleDtoList;
    }

    @Override
    public void updateScheduleById(int id, ScheduleDto scheduleDto){
        Schedule schedule = scheduleMapper.scheduleDtoToScheduleDomain(scheduleDto);
        memoryScheduleStorage.updateScheduleById(id, schedule);
    }

    @Override
    public void deleteScheduleById(int id){
        memoryScheduleStorage.deleteScheduleById(id);
    }

    @Override
    public boolean isExistScheduleId(int id){
        return memoryScheduleStorage.readScheduleById(id) != null;
    }

}
