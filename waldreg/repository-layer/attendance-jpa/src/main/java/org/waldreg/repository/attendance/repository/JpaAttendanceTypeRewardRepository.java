package org.waldreg.repository.attendance.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.attendance.AttendanceTypeReward;

@Repository
public interface JpaAttendanceTypeRewardRepository extends JpaRepository<AttendanceTypeReward, Integer>{

    @Query("select a from AttendanceTypeReward as a where a.name = :name")
    Optional<AttendanceTypeReward> findByName(@Param("name") String name);

}
