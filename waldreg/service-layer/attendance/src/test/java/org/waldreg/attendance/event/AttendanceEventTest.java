package org.waldreg.attendance.event;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.schedule.AttendanceScheduler;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AttendanceScheduler.class})
class AttendanceEventTest{

    @Autowired
    private AttendanceScheduler attendanceScheduler;
//
//    @Test
//    @DisplayName("출석 상 벌점 부여 성공 테스트")
//    void

}
