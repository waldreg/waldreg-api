package org.waldreg.repository.attendance.schedule;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRewardRepository;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;

@Repository
public class AttendanceScheduleRewardRepositoryServiceProvider implements AttendanceScheduleRewardRepository{

    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    AttendanceScheduleRewardRepositoryServiceProvider(JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository){
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
    }

    @Override
    @Transactional
    public void assignRewardToUser(int id, AttendanceType attendanceType){
        Attendance attendance = getAttendance(id);
        attendance.setAttendanceType(getAttendanceTypeReward(attendanceType));
    }

    private Attendance getAttendance(int id){
        LocalDate now = LocalDate.now();
        List<Attendance> attendanceList = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(id, now, now);
        throwIfDoesNotExistAttendance(id, attendanceList);
        return attendanceList.get(0);
    }

    private void throwIfDoesNotExistAttendance(int id, List<Attendance> attendanceList){
        if(attendanceList.size() != 1){
            throw new IllegalStateException("Cannot find Attendance user.id \"" + id + "\"");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRewardTagPresent(AttendanceType attendanceType){
        AttendanceTypeReward attendanceTypeReward = getAttendanceTypeReward(attendanceType);
        return attendanceTypeReward.getRewardTag() != null;
    }

    private AttendanceTypeReward getAttendanceTypeReward(AttendanceType attendanceType){
        return jpaAttendanceTypeRewardRepository.findByName(attendanceType.toString())
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find AttendanceTypeReward named \"" + attendanceType + "\"");});
    }

}
