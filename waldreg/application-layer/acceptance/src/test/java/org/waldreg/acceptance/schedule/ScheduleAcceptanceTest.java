package org.waldreg.acceptance.schedule;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    @BeforeEach
    @AfterEach
    public void INITIATE() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (int month = 1; month <= 12; month++){
            ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                    ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, month, adminToken)
                            .andReturn()
                            .getResponse()
                            .getContentAsString(), ScheduleListResponse.class);
            for (ScheduleResponse scheduleResponse : scheduleListResponse.getScheduleList()){
                ResultActions result = ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, scheduleResponse.getId(), adminToken);
                result.andExpectAll(
                        MockMvcResultMatchers.status().isBadRequest(),
                        MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                        MockMvcResultMatchers.header().string("api-version", apiVersion),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.messages").value("Unknown schedule"),
                        MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
                );
            }
        }
    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 없음")
    public void CREATE_NEW_SCHEDULE_WITHOUT_REPEAT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());


    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 있음")
    public void CREATE_NEW_SCHEDULE_WITH_REPEAT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 month")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_MONTH_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String wrongMonthStartedAt = "2023-13-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(wrongMonthStartedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid month"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 year")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_YEAR_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String wrongYearStartedAt = "1999-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(wrongYearStartedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid year"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 cycle")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_CYCLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int wrongCycle = 0;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(wrongCycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid cycle"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 반복 종료 날짜")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_REPEAT_FINISH_DATE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String wrongRepeatFinishAt = "2023-01-28T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(wrongRepeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid repeat finish date"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 빈 schedule title")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_BLANK_SCHEDULE_TITLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String blankScheduleTitle = "";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(blankScheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule title cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 일정 종료 날짜가 시작날짜보다 앞서거나 같음")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_END_DATE_PRECEDE_START_DATE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(finishAt)
                .finishAt(startedAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("The end date of a Schedule cannot precede the start date."),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 권한 없음")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String wrongToken = "";

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, wrongToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - schedule content 글자 제한 초과")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_CONTENT_OVERFLOW_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = createOverflow();
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Overflow content"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 일정 조회 성공 테스트")
    public void INQUIRY_SPECIFIC_SCHEDULE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.inquirySpecificSchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.schedule_title").value(scheduleRequest.getScheduleTitle()),
                MockMvcResultMatchers.jsonPath("$.schedule_content").value(scheduleRequest.getScheduleContent()),
                MockMvcResultMatchers.jsonPath("$.started_at").value(scheduleRequest.getStartedAt()),
                MockMvcResultMatchers.jsonPath("$.finish_at").value(scheduleRequest.getFinishAt()),
                MockMvcResultMatchers.jsonPath("$.repeat.cycle").value(scheduleRequest.getRepeat().getCycle()),
                MockMvcResultMatchers.jsonPath("#.repeat.repeat_finish_at").value(scheduleRequest.getRepeat().getRepeatFinishAt())
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 일정 조회 실패 테스트 - 없는 schedule id")
    public void INQUIRY_SPECIFIC_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = 0;

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.inquirySpecificSchedule(mvc, id, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown schedule"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 조회 성공 테스트")
    public void INQUIRY_SCHEDULE_BY_TERM_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String scheduleTitle2 = "seminar2";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        ScheduleRequest scheduleRequest2 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .build();
        String scheduleTitle3 = "Mentoring Day";
        String scheduleContent3 = "Graduate Mentoring";
        String startedAt3 = "2023-01-17T20:52";
        String finishAt3 = "2023-01-17T23:59";
        int cycle = 7;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(startedAt3)
                .repeat(repeatScheduleRequest)
                .finishAt(finishAt3)
                .build();
        int year = 2023;
        int month = 1;

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest2));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest3));
        ResultActions result = ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, year, month, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].schedule_title").value(scheduleRequest3.getScheduleTitle()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].started_at").value(scheduleRequest3.getStartedAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].finish_at").value(scheduleRequest3.getFinishAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].repeat.cycle").value(scheduleRequest3.getRepeat().getCycle()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].repeat.repeat_finish_at").value(scheduleRequest3.getRepeat().getRepeatFinishAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].schedule_title").value(scheduleRequest3.getScheduleTitle()),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].started_at").value(scheduleRequest3.getStartedAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].finish_at").value(scheduleRequest3.getFinishAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].repeat.cycle").value(scheduleRequest3.getRepeat().getCycle()),
                MockMvcResultMatchers.jsonPath("$.schedules.[1].repeat.repeat_finish_at").value(scheduleRequest3.getRepeat().getRepeatFinishAt())
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 조회 실패 테스트 - 잘못된 month")
    public void INQUIRY_SCHEDULE_BY_TERM_FAIL_CAUSE_INVALID_MONTH_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String StartedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(StartedAt)
                .finishAt(finishAt)
                .build();
        String scheduleTitle2 = "seminar2";
        String scheduleContent2 = "DFS";
        String StartedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        ScheduleRequest scheduleRequest2 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(StartedAt2)
                .finishAt(finishAt2)
                .build();
        String scheduleTitle3 = "Mentoring Day";
        String scheduleContent3 = "Graduate Mentoring";
        String StartedAt3 = "2023-01-17T20:52";
        String finishAt3 = "2023-01-17T23:59";
        int cycle = 7;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(StartedAt3)
                .repeat(repeatScheduleRequest)
                .finishAt(finishAt3)
                .build();
        int year = 2023;
        int wrongMonth = 13;

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest2));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest3));
        ResultActions result = ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, year, wrongMonth, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid month"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 조회 실패 테스트 - 잘못된 year")
    public void INQUIRY_SCHEDULE_BY_TERM_FAIL_CAUSE_INVALID_YEAR_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String scheduleTitle2 = "seminar2";
        String scheduleContent2 = "DFS";
        String startedAt2 = "2023-02-01T20:52";
        String finishAt2 = "2023-02-07T23:59";
        ScheduleRequest scheduleRequest2 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle2)
                .scheduleContent(scheduleContent2)
                .startedAt(startedAt2)
                .finishAt(finishAt2)
                .build();
        String scheduleTitle3 = "Mentoring Day";
        String scheduleContent3 = "Graduate Mentoring";
        String startedAt3 = "2023-01-17T20:52";
        String finishAt3 = "2023-01-17T23:59";
        int cycle = 7;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(startedAt3)
                .repeat(repeatScheduleRequest)
                .finishAt(finishAt3)
                .build();
        int wrongYear = 1999;
        int Month = 12;

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest2));
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest3));
        ResultActions result = ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, wrongYear, Month, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid year"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 성공 테스트")
    public void MODIFY_SCHEDULE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 잘못된 month")
    public void MODIFY_SCHEDULE_CAUSE_INVALID_MONTH_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String wrongModifiedStartedAt = "2023-13-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(wrongModifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid month"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 잘못된 year")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_INVALID_YEAR_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String wrongModifiedStartedAt = "1999-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(wrongModifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid year"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 잘못된 cycle")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_INVALID_CYCLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int wrongModifiedCycle = 0;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(wrongModifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid cycle"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 잘못된 반복 종료 날짜")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_INVALID_REPEAT_FINISH_DATE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String wrongModifiedRepeatFinishAt = "2023-01-28T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(wrongModifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid repeat finish date"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 빈 schedule title")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_BLANK_SCHEDULE_TITLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String wrongModifiedScheduleTitle = "";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(wrongModifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule title cannot be blank"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 일정 종료 날짜가 시작 날짜보다 앞서거나 같음")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_END_DATE_PRECEDE_START_DATE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedfinishAt)
                .finishAt(modifiedStartedAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("The end date of a Schedule cannot precede the start date."),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 권한 없음")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = "";

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - schedule content 글자 제한 초과")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_CONTENT_OVERFLOW_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .build();
        String modifiedScheduleTitle = "seminar";
        String wrongModifiedScheduleContent = createOverflow();
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(wrongModifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Overflow content"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 삭제 성공 테스트")
    public void DELETE_SCHEDULE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 삭제 실패 테스트 - 권한 없음")
    public void DELETE_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String wrongToken = "";

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        RepeatScheduleRequest repeatScheduleRequest = RepeatScheduleRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(repeatScheduleRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    private String createOverflow(){
        String content = "";
        for (int i = 0; i < 1005; i++){
            content += "A";
        }
        return content;
    }

    public static class ScheduleRequest{

        @JsonProperty("schedule_title")
        private String scheduleTitle;
        @JsonProperty("scheduleContent")
        private String scheduleContent;
        @JsonProperty("startedAt")
        private String startedAt;
        @JsonProperty("finishAt")
        private String finishAt;
        @JsonProperty("repeat")
        private RepeatScheduleRequest repeat = null;

        private ScheduleRequest(){}

        private ScheduleRequest(Builder builder){
            this.scheduleTitle = builder.scheduleTitle;
            this.scheduleContent = builder.scheduleContent;
            this.startedAt = builder.startedAt;
            this.finishAt = builder.finishAt;
            this.repeat = builder.repeat;
        }

        public static Builder builder(){return new Builder();}

        public String getScheduleTitle(){
            return scheduleTitle;
        }

        public void setScheduleTitle(String scheduleTitle){
            this.scheduleTitle = scheduleTitle;
        }

        public String getScheduleContent(){
            return scheduleContent;
        }

        public void setScheduleContent(String scheduleContent){
            this.scheduleContent = scheduleContent;
        }

        public String getStartedAt(){
            return startedAt;
        }

        public void setStartedAt(String startedAt){
            this.startedAt = startedAt;
        }

        public String getFinishAt(){
            return finishAt;
        }

        public void setFinishAt(String finishAt){
            this.finishAt = finishAt;
        }

        public RepeatScheduleRequest getRepeat(){
            return repeat;
        }

        public void setRepeat(RepeatScheduleRequest repeat){
            this.repeat = repeat;
        }

        public final static class Builder{

            private String scheduleTitle;
            private String scheduleContent;
            private String startedAt;
            private String finishAt;
            private RepeatScheduleRequest repeat;

            private Builder(){}

            public Builder scheduleTitle(String scheduleTitle){
                this.scheduleTitle = scheduleTitle;
                return this;
            }

            public Builder scheduleContent(String scheduleContent){
                this.scheduleContent = scheduleContent;
                return this;
            }

            public Builder startedAt(String startedAt){
                this.startedAt = startedAt;
                return this;
            }

            public Builder finishAt(String finishAt){
                this.finishAt = finishAt;
                return this;
            }

            public Builder repeat(RepeatScheduleRequest repeat){
                this.repeat = repeat;
                return this;
            }

            public ScheduleRequest build(){return new ScheduleRequest(this);}

        }

    }

    public static class RepeatScheduleRequest{

        @JsonProperty("cycle")
        private int cycle;
        @JsonProperty("repeat_finish_at")
        private String repeatFinishAt;

        public RepeatScheduleRequest(){}

        private RepeatScheduleRequest(Builder builder){
            this.cycle = builder.cycle;
            this.repeatFinishAt = builder.repeatFinishAt;
        }

        public static Builder builder(){return new Builder();}

        public int getCycle(){
            return cycle;
        }

        public void setCycle(int cycle){
            this.cycle = cycle;
        }

        public String getRepeatFinishAt(){
            return repeatFinishAt;
        }

        public void setRepeatFinishAt(String repeatFinishAt){
            this.repeatFinishAt = repeatFinishAt;
        }

        public final static class Builder{

            private int cycle;
            private String repeatFinishAt;

            private Builder(){}

            public Builder cycle(int cycle){
                this.cycle = cycle;
                return this;
            }

            public Builder repeatFinishAt(String repeatFinishAt){
                this.repeatFinishAt = repeatFinishAt;
                return this;
            }

            public RepeatScheduleRequest build(){return new RepeatScheduleRequest(this);}

        }

    }

    public static class ScheduleResponse{

        @JsonProperty("id")
        private int id;
        @JsonProperty("schedule_title")
        private String scheduleTitle;
        @JsonProperty("scheduleContent")
        private String scheduleContent;
        @JsonProperty("startedAt")
        private String startedAt;
        @JsonProperty("finishAt")
        private String finishAt;
        @JsonProperty("repeat")
        private RepeatScheduleResponse repeat = null;

        private ScheduleResponse(){}

        private ScheduleResponse(Builder builder){
            this.scheduleTitle = builder.scheduleTitle;
            this.scheduleContent = builder.scheduleContent;
            this.startedAt = builder.startedAt;
            this.finishAt = builder.finishAt;
            this.repeat = builder.repeat;
        }

        public static Builder builder(){return new Builder();}

        public int getId(){
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        public String getScheduleTitle(){
            return scheduleTitle;
        }

        public void setScheduleTitle(String scheduleTitle){
            this.scheduleTitle = scheduleTitle;
        }

        public String getScheduleContent(){
            return scheduleContent;
        }

        public void setScheduleContent(String scheduleContent){
            this.scheduleContent = scheduleContent;
        }

        public String getStartedAt(){
            return startedAt;
        }

        public void setStartedAt(String startedAt){
            this.startedAt = startedAt;
        }

        public String getFinishAt(){
            return finishAt;
        }

        public void setFinishAt(String finishAt){
            this.finishAt = finishAt;
        }

        public RepeatScheduleResponse getRepeat(){
            return repeat;
        }

        public void setRepeat(RepeatScheduleResponse repeat){
            this.repeat = repeat;
        }

        public final static class Builder{

            private int id;
            private String scheduleTitle;
            private String scheduleContent;
            private String startedAt;
            private String finishAt;
            private RepeatScheduleResponse repeat;

            private Builder(){}

            public Builder scheduleTitle(String scheduleTitle){
                this.scheduleTitle = scheduleTitle;
                return this;
            }

            public Builder scheduleContent(String scheduleContent){
                this.scheduleContent = scheduleContent;
                return this;
            }

            public Builder startedAt(String startedAt){
                this.startedAt = startedAt;
                return this;
            }

            public Builder finishAt(String finishAt){
                this.finishAt = finishAt;
                return this;
            }

            public Builder repeat(RepeatScheduleResponse repeat){
                this.repeat = repeat;
                return this;
            }

            public ScheduleResponse build(){return new ScheduleResponse(this);}

        }


    }

    public static class RepeatScheduleResponse{

        @JsonProperty("cycle")
        private int cycle;
        @JsonProperty("repeat_finish_at")
        private String repeatFinishAt;

        public RepeatScheduleResponse(){}

        private RepeatScheduleResponse(Builder builder){
            this.cycle = builder.cycle;
            this.repeatFinishAt = builder.repeatFinishAt;
        }

        public static Builder builder(){return new Builder();}

        public int getCycle(){
            return cycle;
        }

        public void setCycle(int cycle){
            this.cycle = cycle;
        }

        public String getRepeatFinishAt(){
            return repeatFinishAt;
        }

        public void setRepeatFinishAt(String repeatFinishAt){
            this.repeatFinishAt = repeatFinishAt;
        }

        public final static class Builder{

            private int cycle;
            private String repeatFinishAt;

            private Builder(){}

            public Builder cycle(int cycle){
                this.cycle = cycle;
                return this;
            }

            public Builder repeatFinishAt(String repeatFinishAt){
                this.repeatFinishAt = repeatFinishAt;
                return this;
            }

            public RepeatScheduleResponse build(){return new RepeatScheduleResponse(this);}

        }

    }

    public static class ScheduleListResponse{

        @JsonProperty("schedules")
        private List<ScheduleResponse> scheduleList;

        public ScheduleListResponse(){}

        private ScheduleListResponse(Builder builder){
            this.scheduleList = builder.scheduleList;
        }

        public static Builder builder(){return new Builder();}

        public List<ScheduleResponse> getScheduleList(){return scheduleList;}

        public final static class Builder{

            private List<ScheduleResponse> scheduleList;

            private Builder(){}

            public Builder ScheduleList(List<ScheduleResponse> scheduleList){
                this.scheduleList = scheduleList;
                return this;
            }

            public ScheduleListResponse build(){return new ScheduleListResponse(this);}

        }

    }

}
