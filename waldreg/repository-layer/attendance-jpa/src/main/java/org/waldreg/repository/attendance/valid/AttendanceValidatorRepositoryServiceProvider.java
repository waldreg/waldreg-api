package org.waldreg.repository.attendance.valid;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.valid.spi.AttendanceValidatorRepository;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;

@Repository
public class AttendanceValidatorRepositoryServiceProvider implements AttendanceValidatorRepository{

    private final JpaAttendanceRepository jpaAttendanceRepository;
    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    AttendanceValidatorRepositoryServiceProvider(JpaAttendanceRepository jpaAttendanceRepository,
            JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository){
        this.jpaAttendanceRepository = jpaAttendanceRepository;
        this.jpaAttendanceTypeRewardRepository = jpaAttendanceTypeRewardRepository;
    }

    @Override
    @Transactional
    public void confirm(int id){
        AttendanceTypeReward attendancedTypeReward = getAttendancedAttendanceTypeReward();
        Attendance attendance = getAttendance(id);
        attendance.setAttendanceType(attendancedTypeReward);
    }

    private AttendanceTypeReward getAttendancedAttendanceTypeReward(){
        return jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ATTENDANCED.toString())
                .orElseThrow(() -> {throw new IllegalStateException("Cannot find AttendanceTypeReward named \"" + AttendanceType.ATTENDANCED + "\"");});
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

}
