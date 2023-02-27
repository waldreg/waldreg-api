package org.waldreg.repository.attendance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.AttendanceUser;

@Repository
public interface JpaAttendanceUserRepository extends JpaRepository<AttendanceUser, Integer>{

    @Modifying
    @Query("delete from AttendanceUser as au where au.user.id = :id")
    void deleteByUserId(@Param("id") int id);

    boolean existsByUserId(int id);

    Optional<AttendanceUser> findByUserId(int id);

}
