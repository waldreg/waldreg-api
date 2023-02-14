package org.waldreg.attendance.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
import org.waldreg.attendance.schedule.publish.AttendanceStartEvent;
import org.waldreg.attendance.schedule.publish.AttendanceStopEvent;
import org.waldreg.attendance.schedule.subscribe.AttendanceStartedEvent;

@Component
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AttendanceScheduler.class, AttendanceSchedulerTest.class})
class AttendanceSchedulerTest{

    @Autowired
    private AttendanceScheduler attendanceScheduler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private static AttendanceStartedEvent attendanceStartedEvent;

    @EventListener(AttendanceStartedEvent.class)
    public void listenAttendanceStartedEvent(AttendanceStartedEvent attendanceStartedEvent){
        AttendanceSchedulerTest.attendanceStartedEvent = attendanceStartedEvent;
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

    @Test
    @DisplayName("출석 종료 성공 테스트 - 랜덤 멀티 스레드")
    void ATTENDANCE_STOP_SUCCESS_MULTI_THREAD_RANDOM_TEST() throws InterruptedException{
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        List<Callable<Boolean>> runnableList = getCallableList(100);

        // when & then
        executorService.invokeAll(runnableList);
    }

    private List<Callable<Boolean>> getCallableList(int count){
        List<Callable<Boolean>> callableList = new ArrayList<>();
        for(int i = 0; i < count; i++){
            int finalI = i;
            callableList.add(() -> {
                applicationEventPublisher.publishEvent(new AttendanceStartEvent(finalI));
                return true;
            });
            callableList.add(
                    () -> {
                        applicationEventPublisher.publishEvent(new AttendanceStopEvent(finalI));
                        Assertions.assertThrows(DoesNotStartedAttendanceException.class, () -> attendanceScheduler.valid("abc"));
                        return true;
                    }
            );
        }
        return callableList;
    }

}
