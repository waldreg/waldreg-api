package org.waldreg.repository;

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

}
