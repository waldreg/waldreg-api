package org.waldreg.repository.attendance.type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.attendance.type.spi.AttendanceTypeRepository;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;

@Repository
public class AttendanceTypeRepositoryServiceProvider implements AttendanceTypeRepository{

    private final JpaAttendanceTypeRewardRepository jpaAttendanceTypeRepository;

    @Autowired
    AttendanceTypeRepositoryServiceProvider(JpaAttendanceTypeRewardRepository jpaAttendanceTypeRepository){
        this.jpaAttendanceTypeRepository = jpaAttendanceTypeRepository;
    }

    @Override
    @Transactional
    public void createAttendanceTypeIfDoesNotExist(String attendanceType){
        jpaAttendanceTypeRepository.findByName(attendanceType)
                .ifPresentOrElse(a -> {}, () -> jpaAttendanceTypeRepository.save(AttendanceTypeReward.builder()
                                .name(attendanceType)
                                .build())
                );
    }

}
