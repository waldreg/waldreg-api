package org.waldreg.repository.attendance.schedule;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRepository;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.attendance.schedule.mapper.MemoryAttendanceScheduleMapper;

@Repository
public class MemoryAttendanceScheduleRepository implements AttendanceScheduleRepository{

    private final MemoryAttendanceStorage memoryAttendanceStorage;
    private final MemoryAttendanceScheduleMapper memoryAttendanceScheduleMapper;

    @Autowired
    public MemoryAttendanceScheduleRepository(MemoryAttendanceStorage memoryAttendanceStorage
                                                , MemoryAttendanceScheduleMapper memoryAttendanceScheduleMapper){
        this.memoryAttendanceStorage = memoryAttendanceStorage;
        this.memoryAttendanceScheduleMapper = memoryAttendanceScheduleMapper;
    }

    @Override
    public List<AttendanceUserStatusDto> readAttendanceUserStatusList(){
        LocalDate now = LocalDate.now();
        List<Attendance> attendanceList = memoryAttendanceStorage.readAllAttendance(now, now);
        return memoryAttendanceScheduleMapper.attendanceListToAttendanceUserStatusDtoList(attendanceList);
    }

    @Override
    public void createNewAttendanceCalendar(LocalDate localDate){
        memoryAttendanceStorage.stageAttendanceUser(localDate);
    }

}
