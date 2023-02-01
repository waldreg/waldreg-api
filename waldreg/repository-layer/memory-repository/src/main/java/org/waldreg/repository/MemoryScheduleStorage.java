package org.waldreg.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.calendar.Schedule;

@Repository
public class MemoryScheduleStorage{

    private final AtomicInteger atomicInteger;

    private final Map<Integer, Schedule> storage;

    {
        storage = new ConcurrentHashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createSchedule(Schedule schedule){
        schedule.setId(atomicInteger.getAndIncrement());
        storage.put(schedule.getId(), schedule);
    }

    public List<Schedule> readScheduleByTerm(int year, int month){
        List<Schedule> scheduleList = new ArrayList<>();
        for (Map.Entry<Integer, Schedule> scheduleEntry : storage.entrySet()){
            if (isScheduleInTerm(year, month, scheduleEntry.getValue())){
                scheduleList.add(scheduleEntry.getValue());
            }
        }
        return scheduleList;
    }

    private boolean isScheduleInTerm(int year, int month, Schedule schedule){
        return (schedule.getStartedAt().getYear() == year && schedule.getStartedAt().getMonthValue() == month) || (schedule.getFinishAt().getYear() == year && schedule.getFinishAt().getMonthValue() == month);
    }

    public Schedule readScheduleById(int id){
        return storage.get(id);
    }

    public void updateScheduleById(int id, Schedule schedule){
        schedule.setId(id);
        storage.put(id, schedule);
    }

    public void deleteScheduleById(int id){
        storage.remove(id);
    }

    public void deleteAllSchedule(){
        storage.clear();
    }

}
