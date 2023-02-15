package org.waldreg.attendance.schedule;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRepository;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRewardRepository;

@Service
@EnableScheduling
public final class AttendanceScheduler{

    private final AttendanceScheduleRepository attendanceScheduleRepository;
    private final AttendanceScheduleRewardRepository attendanceScheduleRewardRepository;

    @Autowired
    public AttendanceScheduler(AttendanceScheduleRepository attendanceScheduleRepository,
                                AttendanceScheduleRewardRepository attendanceScheduleRewardRepository){
        this.attendanceScheduleRepository = attendanceScheduleRepository;
        this.attendanceScheduleRewardRepository = attendanceScheduleRewardRepository;
    }

    @Scheduled(cron = "0 59 23 * * *")
    public void schedule(){
        assignReward();
        createNewAttendanceCalendar();
    }

    private void assignReward(){
        List<AttendanceUserStatusDto> attendanceUserStatusDtoList = attendanceScheduleRepository.readAttendanceUserStatusList();
        for(AttendanceUserStatusDto attendanceUserStatusDto : attendanceUserStatusDtoList){
            if(attendanceScheduleRewardRepository.isRewardTagPresent(attendanceUserStatusDto.getAttendanceType())){
                attendanceScheduleRewardRepository.assignRewardToUser(attendanceUserStatusDto.getId(), attendanceUserStatusDto.getAttendanceType());
            }
        }
    }

    private void createNewAttendanceCalendar(){
        LocalDateTime localDateTime = LocalDateTime.now();
        if(localDateTime.getHour() == 23){
            localDateTime = localDateTime.plusDays(1);
        }
        attendanceScheduleRepository.createNewAttendanceCalendar(localDateTime.toLocalDate());
    }

}
