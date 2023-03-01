package org.waldreg.repository.attendance.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.type.spi.AttendanceTypeRepository;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.repository.attendance.type.repository.JpaAttendanceTypeRepository;

@Repository
public class AttendanceTypeRepositoryServiceProvider implements AttendanceTypeRepository{

    private final JpaAttendanceTypeRepository jpaAttendanceTypeRepository;

    @Autowired
    AttendanceTypeRepositoryServiceProvider(JpaAttendanceTypeRepository jpaAttendanceTypeRepository){
        this.jpaAttendanceTypeRepository = jpaAttendanceTypeRepository;
    }

    @Override
    @Transactional
    public void createAttendanceType(String attendanceType){
        jpaAttendanceTypeRepository.save(AttendanceTypeReward.builder()
                .name(attendanceType)
                .build());
    }

}
