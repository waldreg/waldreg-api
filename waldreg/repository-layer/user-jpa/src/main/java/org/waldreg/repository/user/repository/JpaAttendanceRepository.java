package org.waldreg.repository.user.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.Attendance;

@Repository("userJpaAttendanceRepository")
public interface JpaAttendanceRepository extends JpaRepository<Attendance, Integer>{

    @Query("select a from Attendance as a where a.attendanceUser.user.id = :id")
    List<Attendance> findByUserId(@Param("id") int id);

    @Modifying
    @Query("delete from Attendance as a where a.attendanceUser.attendanceUserId = :attendanceUserId")
    void deleteByAttendanceUserId(@Param("attendanceUserId") int attendanceUserId);

}
