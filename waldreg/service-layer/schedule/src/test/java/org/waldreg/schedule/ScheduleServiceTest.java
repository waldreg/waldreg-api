package org.waldreg.schedule;

import java.util.ArrayList;
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
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.InvalidSchedulePeriodException;
import org.waldreg.schedule.exception.UnknownScheduleException;
import org.waldreg.schedule.management.DefaultScheduleManager;
import org.waldreg.schedule.management.ScheduleManager;
import org.waldreg.schedule.spi.repository.ScheduleRepository;
import org.waldreg.schedule.spi.schedule.ScheduleIdExistChecker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultScheduleManager.class})
public class ScheduleServiceTest{

    @Autowired
    private ScheduleManager scheduleManager;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private ScheduleIdExistChecker scheduleIdExistChecker;

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
        Assertions.assertThrows(InvalidSchedulePeriodException.class, () -> scheduleManager.createSchedule(scheduleRequest));
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

    @Test
    @DisplayName("특정 일정 조회 성공 테스트")
    public void READ_SPECIFIC_SCHEDULE_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest);
        ScheduleDto result = scheduleManager.readScheduleById(1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(scheduleRequest.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getCycle(), result.getRepeatDto().getCycle()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getRepeatFinishAt(), result.getRepeatDto().getRepeatFinishAt())
        );

    }

    @Test
    @DisplayName("특정 일정 조회 실패 테스트 - 없는 schedule")
    public void READ_SPECIFIC_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST(){
        //given

        //when
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownScheduleException.class, () -> scheduleManager.readScheduleById(1));

    }

    @Test
    @DisplayName("일정 조회 성공 테스트")
    public void READ_SCHEDULE_BY_TERM_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        int cycle2 = 12;
        String repeatFinishAt2 = "2023-10-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        scheduleManager.createSchedule(scheduleRequest2);
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        scheduleDtoList.add(scheduleRequest);
        Mockito.when(scheduleRepository.readScheduleByTerm(Mockito.anyInt(), Mockito.anyInt())).thenReturn(scheduleDtoList);
        List<ScheduleDto> result = scheduleManager.readScheduleByTerm(2023, 1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(scheduleRequest.getScheduleTitle(), result.get(0).getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest.getScheduleContent(), result.get(0).getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest.getStartedAt(), result.get(0).getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest.getFinishAt(), result.get(0).getFinishAt()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getCycle(), result.get(0).getRepeatDto().getCycle()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getRepeatFinishAt(), result.get(0).getRepeatDto().getRepeatFinishAt())
        );

    }

    @Test
    @DisplayName("일정 조회 실패 테스트 - 잘못된 year, month")
    public void READ_SCHEDULE_BY_TERM_FAIL_CAUSE_INVALID_DATE_FORMAT_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        int cycle2 = 12;
        String repeatFinishAt2 = "2023-10-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        scheduleManager.createSchedule(scheduleRequest2);
        List<ScheduleDto> scheduleDtoList = new ArrayList<>();
        scheduleDtoList.add(scheduleRequest);
        Mockito.when(scheduleRepository.readScheduleByTerm(Mockito.anyInt(), Mockito.anyInt())).thenReturn(scheduleDtoList);

        //then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.readScheduleByTerm(1999, 0));

    }

    @Test
    @DisplayName("일정 수정 성공 테스트")
    public void UPDATE_SCHEDULE_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);
        scheduleManager.updateScheduleById(1, scheduleRequest2);
        ScheduleDto result = scheduleManager.readScheduleById(1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(scheduleRequest2.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest2.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest2.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest2.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertEquals(scheduleRequest2.getRepeatDto().getCycle(), result.getRepeatDto().getCycle()),
                () -> Assertions.assertEquals(scheduleRequest2.getRepeatDto().getRepeatFinishAt(), result.getRepeatDto().getRepeatFinishAt())
        );

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 날짜가 잘못된 형식일 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_INVALID_DATE_FORMAT_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-29T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 년도가 2000 미만일 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_UNDER_YEAR_LIMIT_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "1999-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 잘못된 cycle")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_INVALID_CYCLE_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = -1;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidRepeatException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 반복 종료 날짜가 일정 시작 날짜 혹은 끝 날짜보다 앞설 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_REPEAT_FINISH_AT_PRECEDE_SCHEDULE_TERM_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = 10;
        String repeatFinishAt2 = "2023-02-27T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidRepeatException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 반복 종료 날짜가 잘못된 형식일 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_INVALID_REPEAT_FINISH_AT_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-32T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidDateFormatException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 일정 종료 날짜가 시작 날짜를 앞설 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_INVALID_SCHEDULE_PERIOD_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-28T23:59";
        String finishAt2 = "2023-02-01T20:52";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(InvalidSchedulePeriodException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - schedule_content가 1000자를 넘을 때")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_CONTENT_OVERFLOW_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = createOverflow();
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(ContentOverflowException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 없는 schedule")
    public void UPDATE_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST(){
        //given
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "BFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-28T23:59";
        int cycle2 = 100;
        String repeatFinishAt2 = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleRequest2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .repeatDto(repeatScheduleRequest2)
                .build();

        //when
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(false);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest2);

        //then
        Assertions.assertThrows(UnknownScheduleException.class, () -> scheduleManager.updateScheduleById(1, scheduleRequest2));

    }

    @Test
    @DisplayName("일정 삭제 성공 테스트")
    public void DELETE_SCHEDULE_BY_ID_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest);

        //then
        Assertions.assertDoesNotThrow(() -> scheduleManager.deleteScheduleById(1));

    }

    @Test
    @DisplayName("일정 삭제 실패 테스트 - 잘못된 id")
    public void DELETE_SCHEDULE_BY_ID_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatDto repeatScheduleRequest = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleRequest = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatScheduleRequest)
                .build();

        //when
        scheduleManager.createSchedule(scheduleRequest);
        Mockito.when(scheduleIdExistChecker.isExistScheduleId(Mockito.anyInt())).thenReturn(false);
        Mockito.when(scheduleRepository.readScheduleById(Mockito.anyInt())).thenReturn(scheduleRequest);

        //then
        Assertions.assertThrows(UnknownScheduleException.class, () -> scheduleManager.deleteScheduleById(1));

    }

    private String createOverflow(){
        String content = "";
        for (int i = 0; i < 1005; i++){
            content += "A";
        }
        return content;
    }

}
