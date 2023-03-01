package org.waldreg.repository.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository("userJpaUserRepository")
public interface JpaUserRepository extends JpaRepository<User, Integer>{

    @Query("select u from User as u join u.character where u.id = :id")
    Optional<User> findById(@Param("id") int id);

    @Query("select u from User as u join u.character where u.userInfo.userId = :userId")
    Optional<User> findByUserId(@Param("userId") String userId);

    @Modifying
    @Query(value = "delete from Attendance as a where a.attendanceUser.id = (select au.id from AttendanceUser as au where au.user.id = :id)")
    void deleteAttendance(@Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM ATTENDANCE_USER as AU WHERE AU.USER_ID = :id", nativeQuery = true)
    void deleteAttendanceUser(@Param("id") int id);

    @Modifying
    @Query(value = "DELETE FROM USER WHERE USER_ID = :id", nativeQuery = true)
    void deleteById(@Param("id") int id);

    @Query("select case when count(u) > 0 then true else false end from User as u where u.userInfo.userId = :userId")
    boolean existsByUserId(@Param("userId") String userId);

    @Query(value = "SELECT A.* FROM (SELECT U.*, C.CHARACTER_CHARACTER_NAME FROM USER as U JOIN CHARACTER as C WHERE U.CHARACTER_ID = C.CHARACTER_ID ORDER BY U.USER_ID) as A LIMIT :count OFFSET :start", nativeQuery = true)
    List<User> findAll(@Param("start") int start, @Param("count") int count);


}
