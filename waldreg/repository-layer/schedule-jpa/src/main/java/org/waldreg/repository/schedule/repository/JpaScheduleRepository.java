package org.waldreg.repository.schedule.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.calendar.Schedule;

@Repository("scheduleJpaScheduleRepository")
public interface JpaScheduleRepository extends JpaRepository<Schedule, Integer>{

    @Query(value = "SELECT * FROM SCHEDULE WHERE (YEAR(SCHEDULE_STARTED_AT) = :year AND MONTH(SCHEDULE_STARTED_AT) = :month) OR (YEAR(SCHEDULE_FINISH_AT) = :year AND MONTH(SCHEDULE_FINISH_AT) = :month)",nativeQuery = true)
    List<Schedule> findScheduleByTerm(@Param("year")int year,@Param("month") int month);

}
