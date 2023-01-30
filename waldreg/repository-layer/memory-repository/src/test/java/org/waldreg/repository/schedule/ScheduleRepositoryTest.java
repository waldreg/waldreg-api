package org.waldreg.repository.schedule;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.repository.MemoryScheduleStorage;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.spi.ScheduleRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryScheduleRepository.class, MemoryScheduleStorage.class, ScheduleMapper.class})
public class ScheduleRepositoryTest{

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    MemoryScheduleStorage memoryScheduleStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL(){memoryScheduleStorage.deleteAllSchedule();}

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
        RepeatDto repeatDto = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatDto)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> scheduleRepository.createSchedule(scheduleDto));

    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 없을 때")
    public void CREATE_NEW_SCHEDULE_WITHOUT_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> scheduleRepository.createSchedule(scheduleDto));

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
        String scheduleTitle3 = "seminar";
        String scheduleContent3 = "Dijkstra";
        String startedAt3 = "2023-01-16T20:52";
        String finishAt3 = "2023-01-23T23:59";
        ScheduleDto scheduleRequest3 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(startedAt3)
                .finishAt(finishAt3)
                .build();

        //when
        scheduleRepository.createSchedule(scheduleRequest);
        scheduleRepository.createSchedule(scheduleRequest2);
        scheduleRepository.createSchedule(scheduleRequest3);
        List<ScheduleDto> result = scheduleRepository.readScheduleByTerm(2023, 1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(scheduleRequest.getScheduleTitle(), result.get(0).getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest.getScheduleContent(), result.get(0).getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest.getStartedAt(), result.get(0).getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest.getFinishAt(), result.get(0).getFinishAt()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getCycle(), result.get(0).getRepeatDto().getCycle()),
                () -> Assertions.assertEquals(scheduleRequest.getRepeatDto().getRepeatFinishAt(), result.get(0).getRepeatDto().getRepeatFinishAt()),
                () -> Assertions.assertEquals(scheduleRequest3.getScheduleTitle(), result.get(1).getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest3.getScheduleContent(), result.get(1).getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest3.getStartedAt(), result.get(1).getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest3.getFinishAt(), result.get(1).getFinishAt()),
                () -> Assertions.assertNull(result.get(1).getRepeatDto()));
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
        RepeatDto repeatDto = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatDto)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-01-31T20:52";
        String finishAt2 = "2023-02-23T23:59";
        ScheduleDto scheduleDto2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .build();

        //when
        scheduleRepository.createSchedule(scheduleDto);
        scheduleRepository.createSchedule(scheduleDto2);
        List<ScheduleDto> scheduleDtoList = scheduleRepository.readScheduleByTerm(2023, 2);
        System.out.println("!!!" + scheduleDtoList.get(0).getId());
        ScheduleDto result = scheduleRepository.readScheduleById(scheduleDtoList.get(0).getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(scheduleDto2.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleDto2.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(scheduleDto2.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(scheduleDto2.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertNull(result.getRepeatDto())
        );

    }

    @Test
    @DisplayName("일정 수정 성공 테스트 - 반복 삭제하고, schedule content 변경")
    public void UPDATE_SCHEDULE_WITH_DELETE_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-28T23:59";
        RepeatDto repeatDto = RepeatDto.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatDto)
                .build();
        String scheduleContent2 = "DFS";
        ScheduleDto scheduleDto2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();

        //when
        scheduleRepository.createSchedule(scheduleDto);
        List<ScheduleDto> scheduleDtoList = scheduleRepository.readScheduleByTerm(2023, 2);
        scheduleRepository.updateScheduleById(scheduleDtoList.get(0).getId(), scheduleDto2);
        ScheduleDto result = scheduleRepository.readScheduleById(scheduleDtoList.get(0).getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(scheduleDto2.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleDto2.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(scheduleDto2.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(scheduleDto2.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertNull(result.getRepeatDto())
        );

    }

    @Test
    @DisplayName("일정 수정 성공 테스트 - 반복 추가")
    public void UPDATE_SCHEDULE_WITH_ADD_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        ScheduleDto scheduleDto = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        int cycle2 = 123;
        String repeatFinishAt2 = "2023-12-28T23:59";
        RepeatDto repeatDto2 = RepeatDto.builder()
                .cycle(cycle2)
                .repeatFinishAt(repeatFinishAt2)
                .build();
        ScheduleDto scheduleDto2 = ScheduleDto.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeatDto(repeatDto2)
                .build();

        //when
        scheduleRepository.createSchedule(scheduleDto);
        List<ScheduleDto> scheduleDtoList = scheduleRepository.readScheduleByTerm(2023, 2);
        scheduleRepository.updateScheduleById(scheduleDtoList.get(0).getId(), scheduleDto2);
        ScheduleDto result = scheduleRepository.readScheduleById(scheduleDtoList.get(0).getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(scheduleDto2.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleDto2.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(scheduleDto2.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(scheduleDto2.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertEquals(scheduleDto2.getRepeatDto().getCycle(), result.getRepeatDto().getCycle()),
                () -> Assertions.assertEquals(scheduleDto2.getRepeatDto().getRepeatFinishAt(), result.getRepeatDto().getRepeatFinishAt())
        );

    }

}
