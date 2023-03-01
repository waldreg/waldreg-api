package org.waldreg.repository.attendance.waiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.waiver.Waiver;

@Repository("attendanceJpaWaiverRepository")
public interface JpaWaiverRepository extends JpaRepository<Waiver, Integer>{
}
