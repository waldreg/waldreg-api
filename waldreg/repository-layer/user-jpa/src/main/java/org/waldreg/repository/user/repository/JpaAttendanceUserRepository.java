package org.waldreg.repository.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.user.User;

@Repository("userJpaAttendanceUserRepository")
public interface JpaAttendanceUserRepository extends JpaRepository<AttendanceUser, Integer>{

    boolean existsByUser(User user);

    AttendanceUser findByUser(User user);

}
