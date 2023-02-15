package org.waldreg.attendance.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.exception.DoesNotStartedAttendanceException;
import org.waldreg.attendance.exception.WrongAttendanceIdentifyException;
import org.waldreg.attendance.event.publish.AttendanceStartEvent;
import org.waldreg.attendance.event.publish.AttendanceStopEvent;
import org.waldreg.attendance.event.subscribe.AttendanceStartedEvent;

@Component
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AttendanceEvent.class, AttendanceEventTest.class})
class AttendanceEventTest{

    @Autowired
    private AttendanceEvent attendanceScheduler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private static AttendanceStartedEvent attendanceStartedEvent;

    @EventListener(AttendanceStartedEvent.class)
    public void listenAttendanceStartedEvent(AttendanceStartedEvent attendanceStartedEvent){
        AttendanceEventTest.attendanceStartedEvent = attendanceStartedEvent;
    }

    @Test
    @DisplayName("출석 시작 성공 테스트")
    void ATTENDANCE_START_SUCCESS_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(1));
        String identify = attendanceStartedEvent.getAttendanceIdentify();
        int starterId = attendanceStartedEvent.getAttendanceStarterId();

        // when & then
        Assertions.assertNotNull(identify);
    }

    @Test
    @DisplayName("출석 번호 비교 성공 테스트")
    void ATTENDANCE_IDENTIFY_VALID_SUCCESS_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(1));
        String identify = attendanceStartedEvent.getAttendanceIdentify();

        // when & then
        Assertions.assertDoesNotThrow(() -> attendanceScheduler.valid(identify));
    }

    @Test
    @DisplayName("출석 번호 비교 실패 테스트 - 출석 을 시작하지 않음")
    void ATTENDANCE_IDENTIFY_VALID_FAIL_NOT_STARTED_ATTENDANCE_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStopEvent(1));
        String identify = "123";

        // when & then
        Assertions.assertThrows(DoesNotStartedAttendanceException.class, () -> attendanceScheduler.valid(identify));
    }

    @Test
    @DisplayName("출석 번호 비교 실패 테스트 - 출석 번호가 다름")
    void ATTENDANCE_IDENTIFY_VALID_FAIL_WRONG_ATTENDANCE_IDENTIFY_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(2));
        String identify = attendanceStartedEvent.getAttendanceIdentify() + "1";

        // when
        Assertions.assertThrows(WrongAttendanceIdentifyException.class, () -> attendanceScheduler.valid(identify));
    }

    @Test
    @DisplayName("출석 종료 성공 테스트")
    void ATTENDANCE_STOP_SUCCESS_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(1));

        // when
        applicationEventPublisher.publishEvent(new AttendanceStopEvent(1));

        // then
        Assertions.assertThrows(DoesNotStartedAttendanceException.class, () -> attendanceScheduler.valid("123"));
    }

    @Test
    @DisplayName("출석 시작 멀티 스레드 테스트")
    void ATTENDANCE_START_MULTI_THREAD_TEST() throws Exception{
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(1));
        Assertions.assertEquals(1, attendanceStartedEvent.getAttendanceStarterId());
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(2));
        Assertions.assertEquals(2, attendanceStartedEvent.getAttendanceStarterId());
    }

    @Test
    @DisplayName("출석 종료 성공 테스트 - 멀티스레드")
    void ATTENDANCE_STOP_SUCCESS_MULTI_THREAD_TEST(){
        // given
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(1));
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(2));
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(3));
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(4));
        applicationEventPublisher.publishEvent(new AttendanceStartEvent(5));

        // when
        applicationEventPublisher.publishEvent(new AttendanceStopEvent(6));

        // then
        Assertions.assertThrows(DoesNotStartedAttendanceException.class, () -> attendanceScheduler.valid("123"));
    }

}
