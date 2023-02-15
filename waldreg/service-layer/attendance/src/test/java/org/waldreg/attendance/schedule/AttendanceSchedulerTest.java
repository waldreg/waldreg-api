package org.waldreg.attendance.schedule;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRepository;
import org.waldreg.attendance.schedule.spi.AttendanceScheduleRewardRepository;
import org.waldreg.attendance.type.AttendanceType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AttendanceScheduler.class})
class AttendanceSchedulerTest{

    @Autowired
    private AttendanceScheduler attendanceScheduler;

    @MockBean
    private AttendanceScheduleRepository attendanceScheduleRepository;

    @MockBean
    private AttendanceScheduleRewardRepository attendanceScheduleRewardRepository;

    @Test
    @DisplayName("출석별 상,벌점 부여 성공 테스트")
    void SCHEDULE_REWARD_TEST(){
        // given
        AttendanceUserStatusDto attendanceUserStatusDto = AttendanceUserStatusDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .build();

        // when
        Mockito.when(attendanceScheduleRepository.readAttendanceUserStatusList()).thenReturn(List.of(attendanceUserStatusDto));
        Mockito.when(attendanceScheduleRewardRepository.isRewardTagPresent(Mockito.any())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(attendanceScheduler::schedule);
    }

}
