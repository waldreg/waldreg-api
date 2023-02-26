package org.waldreg.repository.schedule.repository;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.calendar.Schedule;
import org.waldreg.domain.calendar.ScheduleRepeat;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaScheduleRepositoryTest{

    @Autowired
    private JpaScheduleRepository jpaScheduleRepository;

    @Autowired
    private EntityManager entityManager;

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
        ScheduleRepeat scheduleRepeat = ScheduleRepeat.builder()
                .cycle(cycle)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt))
                .build();
        Schedule schedule = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .scheduleRepeat(scheduleRepeat)
                .build();

        //when
        Schedule result = jpaScheduleRepository.saveAndFlush(schedule);

        //then
        assertAll(schedule, result);

    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 없을 때")
    public void CREATE_NEW_SCHEDULE_WITHOUT_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        Schedule schedule = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .build();

        //when
        Schedule result = jpaScheduleRepository.saveAndFlush(schedule);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(schedule.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(schedule.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(schedule.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(schedule.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertNull(result.getScheduleRepeat())
        );

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
        ScheduleRepeat scheduleRepeat = ScheduleRepeat.builder()
                .cycle(cycle)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt))
                .build();
        Schedule scheduleRequest = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .scheduleRepeat(scheduleRepeat)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        int cycle2 = 12;
        String repeatFinishAt2 = "2023-10-31T23:59";
        ScheduleRepeat scheduleRepeat2 = ScheduleRepeat.builder()
                .cycle(cycle2)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt2))
                .build();
        Schedule scheduleRequest2 = Schedule.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(LocalDateTime.parse(startedAt2))
                .finishAt(LocalDateTime.parse(finishAt2))
                .scheduleRepeat(scheduleRepeat2)
                .build();
        String scheduleTitle3 = "seminar";
        String scheduleContent3 = "Dijkstra";
        String startedAt3 = "2023-01-16T20:52";
        String finishAt3 = "2023-01-23T23:59";
        Schedule scheduleRequest3 = Schedule.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(LocalDateTime.parse(startedAt3))
                .finishAt(LocalDateTime.parse(finishAt3))
                .build();

        //when
        jpaScheduleRepository.saveAndFlush(scheduleRequest);
        jpaScheduleRepository.saveAndFlush(scheduleRequest2);
        jpaScheduleRepository.saveAndFlush(scheduleRequest3);
        List<Schedule> result = jpaScheduleRepository.findScheduleByTerm(2023, 1);

        //then
        assertAll(scheduleRequest, result.get(0));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(scheduleRequest3.getScheduleTitle(), result.get(1).getScheduleTitle()),
                () -> Assertions.assertEquals(scheduleRequest3.getScheduleContent(), result.get(1).getScheduleContent()),
                () -> Assertions.assertEquals(scheduleRequest3.getStartedAt(), result.get(1).getStartedAt()),
                () -> Assertions.assertEquals(scheduleRequest3.getFinishAt(), result.get(1).getFinishAt()),
                () -> Assertions.assertNull(result.get(1).getScheduleRepeat()));
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
        ScheduleRepeat scheduleRepeat = ScheduleRepeat.builder()
                .cycle(cycle)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt))
                .build();
        Schedule scheduleRequest = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .scheduleRepeat(scheduleRepeat)
                .build();
        String scheduleTitle2 = "seminar";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-01-31T20:52";
        String finishAt2 = "2023-02-23T23:59";
        Schedule scheduleRequest2 = Schedule.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(LocalDateTime.parse(startedAt2))
                .finishAt(LocalDateTime.parse(finishAt2))
                .build();

        //when
        Schedule schedule = jpaScheduleRepository.saveAndFlush(scheduleRequest);
        jpaScheduleRepository.saveAndFlush(scheduleRequest2);
        Schedule result = jpaScheduleRepository.findById(schedule.getId()).get();

        //then
        assertAll(schedule, result);
        Assertions.assertEquals(schedule.getId(), result.getId());
    }

    @Test
    @DisplayName("일정 수정 성공 테스트 - 반복 삭제")
    public void UPDATE_SCHEDULE_WITH_DELETE_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeat scheduleRepeat = ScheduleRepeat.builder()
                .cycle(cycle)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt))
                .build();
        Schedule scheduleRequest = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .scheduleRepeat(scheduleRepeat)
                .build();

        //when
        Schedule schedule = jpaScheduleRepository.saveAndFlush(scheduleRequest);
        schedule.setScheduleContent("blablabla");
        schedule.setScheduleRepeat(null);
        entityManager.flush();
        entityManager.clear();
        Schedule result = jpaScheduleRepository.findById(schedule.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(schedule.getId(), result.getId()),
                () -> Assertions.assertEquals(schedule.getScheduleTitle(), result.getScheduleTitle()),
                () -> Assertions.assertEquals(schedule.getScheduleContent(), result.getScheduleContent()),
                () -> Assertions.assertEquals(schedule.getStartedAt(), result.getStartedAt()),
                () -> Assertions.assertEquals(schedule.getFinishAt(), result.getFinishAt()),
                () -> Assertions.assertNull(result.getScheduleRepeat()));
    }

    @Test
    @DisplayName("일정 수정 성공 테스트 - 반복 추가")
    public void UPDATE_SCHEDULE_WITH_ADD_REPEAT_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeat scheduleRepeat = ScheduleRepeat.builder()
                .cycle(cycle)
                .repeatFinishAt(LocalDateTime.parse(repeatFinishAt))
                .build();
        Schedule scheduleRequest = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .build();

        //when
        Schedule schedule = jpaScheduleRepository.saveAndFlush(scheduleRequest);
        schedule.setScheduleContent("blablabla");
        schedule.setScheduleRepeat(scheduleRepeat);
        entityManager.flush();
        entityManager.clear();
        Schedule result = jpaScheduleRepository.findById(schedule.getId()).get();

        //then
        assertAll(schedule, result);
        Assertions.assertEquals(schedule.getId(), result.getId());
    }

    @Test
    @DisplayName("일정 삭제 성공 테스트")
    public void DELETE_SCHEDULE_BY_ID_SUCCESS_TEST(){
        //given
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-31T20:52";
        String finishAt = "2023-02-23T23:59";
        Schedule schedule = Schedule.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(LocalDateTime.parse(startedAt))
                .finishAt(LocalDateTime.parse(finishAt))
                .build();

        //when
        Schedule result = jpaScheduleRepository.saveAndFlush(schedule);
        jpaScheduleRepository.deleteById(result.getId());

        //then
        Assertions.assertFalse(() -> jpaScheduleRepository.existsById(result.getId()));

    }

    private void assertAll(Schedule expected, Schedule actual){
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected.getScheduleTitle(), actual.getScheduleTitle()),
                () -> Assertions.assertEquals(expected.getScheduleContent(), actual.getScheduleContent()),
                () -> Assertions.assertEquals(expected.getStartedAt(), actual.getStartedAt()),
                () -> Assertions.assertEquals(expected.getFinishAt(), actual.getFinishAt()),
                () -> Assertions.assertEquals(expected.getScheduleRepeat().getCycle(), actual.getScheduleRepeat().getCycle()),
                () -> Assertions.assertEquals(expected.getScheduleRepeat().getRepeatFinishAt(), actual.getScheduleRepeat().getRepeatFinishAt())
        );
    }

}
