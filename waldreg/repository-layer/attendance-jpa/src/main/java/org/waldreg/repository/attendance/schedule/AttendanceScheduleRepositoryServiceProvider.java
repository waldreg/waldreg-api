package org.waldreg.repository.attendance.schedule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.attendance.schedule.mapper.ScheduleMapper;

@Repository
public class AttendanceScheduleRepositoryServiceProvider implements AttendanceScheduleRepository{

    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceUserRepository jpaAttendanceUserRepository;
    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;
    private final ScheduleMapper scheduleMapper;

    @Autowired
    AttendanceScheduleRepositoryServiceProvider(JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceUserRepository jpaAttendanceUserRepository,
            JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository,
            ScheduleMapper scheduleMapper){
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceUserRepository = jpaAttendanceUserRepository;
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
        this.scheduleMapper = scheduleMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceUserStatusDto> readAttendanceUserStatusList(){
        LocalDate now = LocalDate.now();
        List<Attendance> attendanceList = jpaAttendanceRepository.findBetweenAttendanceDate(now, now);
        return attendanceList.stream()
                .map(scheduleMapper::attendanceToAttendanceUserStatusDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createNewAttendanceCalendar(LocalDate localDate){
        Map<Integer, Attendance> existAttendanceMap = convertListToMap(jpaAttendanceRepository.findBetweenAttendanceDate(localDate, localDate));
        List<AttendanceUser> attendanceUserList = jpaAttendanceUserRepository.findAll();
        List<Attendance> attendanceListToAdd = new ArrayList<>();
        attendanceUserList.forEach(au -> {
            if(existAttendanceMap.get(au.getAttendanceUserId()) == null){
                attendanceListToAdd.add(
                        Attendance.builder().user(au).attendanceDate(localDate).attendanceType(getAbsenceAttendanceTypeReward()).build()
                );
            }
        });
        jpaAttendanceRepository.saveAll(attendanceListToAdd);
    }

    private Map<Integer, Attendance> convertListToMap(List<Attendance> attendanceList){
        return attendanceList.stream().collect(Collectors.toMap(a -> a.getAttendanceUser().getAttendanceUserId(), a -> a));
    }

    private AttendanceTypeReward getAbsenceAttendanceTypeReward(){
        return jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString())
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find AttendanceTypeReward named \"" + AttendanceType.ABSENCE + "\"");});
    }

}
