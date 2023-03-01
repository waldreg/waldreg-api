package org.waldreg.repository.attendance.type.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.AttendanceTypeReward;

@Repository
public interface JpaAttendanceTypeRepository extends JpaRepository<AttendanceTypeReward, Integer>{

}
