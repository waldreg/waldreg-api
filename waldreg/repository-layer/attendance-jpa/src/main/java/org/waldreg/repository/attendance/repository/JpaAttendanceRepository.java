package org.waldreg.repository.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.Attendance;

@Repository
public interface JpaAttendanceRepository extends JpaRepository<Attendance, Integer>{

    @Query("select a from Attendance as a where :from <= a.attendanceDate and a.attendanceDate <= :to")
    List<Attendance> findBetweenAttendanceDate(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("select a from Attendance as a where a.attendanceUser.user.id = :id and :from <= a.attendanceDate and a.attendanceDate <= :to")
    List<Attendance> findSpecificBetweenAttendanceDate(@Param("id") int id, @Param("from") LocalDate from, @Param("to") LocalDate to);

}
