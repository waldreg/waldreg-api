package org.waldreg.schedule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.StartedAtIsAfterFinishAtException;
import org.waldreg.schedule.management.DefaultScheduleManager;
import org.waldreg.schedule.management.ScheduleManager;
import org.waldreg.schedule.spi.ScheduleRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultScheduleManager.class})
public class ScheduleServiceTest{

    @Autowired
    private ScheduleManager scheduleManager;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 있을 때")
    public void CREATE_NEW_SCHEDULE_WITH_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-28T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> scheduleManager.createSchedule(scheduleRequest));

    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 없을 때")
    public void CREATE_NEW_SCHEDULE_WITHOUT_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> scheduleManager.createSchedule(scheduleRequest));

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - year가 2000보다 작을 때")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_YEAR_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "1999-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.createSchedule(scheduleRequest));
    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 날짜 형식")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_DATE_FORMAT_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-29T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.createSchedule(scheduleRequest));
    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 일정 끝 날짜가 일정 시작 날짜보다 앞섬")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_END_DATE_PRECEDE_START_DATE_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-02-04T23:59";
        String finishAt = "2023-01-31T20:52";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(StartedAtIsAfterFinishAtException.class, () -> scheduleManager.createSchedule(scheduleRequest));
    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 cycle")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_CYCLE_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        int cycle = 0;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(InvalidRepeatException.class, () -> scheduleManager.createSchedule(scheduleRequest));

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 반복 종료 날짜가 일정의 종료일보다 빠른 경우")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_INVALID_REPEAT_FINISH_DATE_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-02-22T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(InvalidRepeatException.class, () -> scheduleManager.createSchedule(scheduleRequest));

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - schedule_content가 1000자 초과")
    public void CREATE_NEW_SCHEDULE_WITH_REPEAT_FAIL_CAUSE_CONTENT_OVERFLOW_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = createOverflow();
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatRequest)
                .build();

        //when&then
        Assertions.assertThrows(ContentOverflowException.class, () -> scheduleManager.createSchedule(scheduleRequest));

    }

    private String createOverflow(){
        String content = "";
        for (int i = 0; i < 1005; i++){
            content += "A";
        }
        return content;
    }

}
