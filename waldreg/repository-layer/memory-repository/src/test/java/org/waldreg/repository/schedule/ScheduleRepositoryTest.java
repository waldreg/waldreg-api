package org.waldreg.repository.schedule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.repository.MemoryScheduleStorage;
import org.waldreg.schedule.dto.RepeatDto;
import org.waldreg.schedule.dto.ScheduleDto;
import org.waldreg.schedule.spi.repository.ScheduleRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryScheduleRepository.class, MemoryScheduleStorage.class, ScheduleMapper.class})
public class ScheduleRepositoryTest{

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    MemoryScheduleStorage memoryScheduleStorage;

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


}
