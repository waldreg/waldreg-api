package org.waldreg.repository.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository("userJpaUserRepository")
public interface JpaUserRepository extends JpaRepository<User, Integer>{

    @Modifying
    @Query(value = "delete from Attendance as a where a.attendanceUser.id = (select au.id from AttendanceUser as au where au.user.id = :id)")
    void deleteAttendance(@Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM ATTENDANCE_USER as AU WHERE AU.USER_ID = :id", nativeQuery = true)
    void deleteAttendanceUser(@Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM USER WHERE USER_ID = :id", nativeQuery = true)
    void deleteById(@Param("id") int id);

    boolean existsByUserInfoUserId(String userId);

}
