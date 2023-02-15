package org.waldreg.acceptance.schedule;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
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
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.schedule.request.ScheduleRepeatRequest;
import org.waldreg.controller.schedule.request.ScheduleRequest;
import org.waldreg.controller.schedule.response.ScheduleListResponse;
import org.waldreg.controller.schedule.response.ScheduleResponse;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();


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
                ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, scheduleResponse.getId(), adminToken);
                ScheduleAcceptanceTestHelper.inquirySpecificSchedule(mvc,scheduleResponse.getId(),adminToken)
                        .andExpectAll(
                                MockMvcResultMatchers.status().isBadRequest(),
                                MockMvcResultMatchers.header().string("api-version", apiVersion),
                                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-410"),
                                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find schedule with id \""+scheduleResponse.getId()+"\""),
                                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
                        );
            }
        }
    }

    @BeforeEach
    @AfterEach
    public void INITIATE_USER() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (UserRequest request : userCreateRequestList){
            UserResponse userResponse = objectMapper.readValue(UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), UserResponse.class);
            UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), adminToken);
            ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId());
            result.andExpectAll(
                    MockMvcResultMatchers.status().isBadRequest(),
                    MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                    MockMvcResultMatchers.header().string("api-version", apiVersion),
                    MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                    MockMvcResultMatchers.jsonPath("$.code").value("USER-406"),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id \""+request.getUserId()+"\""),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
        userCreateRequestList.clear();
    }

    @Test
    @DisplayName("새로운 일정 생성 성공 테스트 - 반복 없음")
    public void CREATE_NEW_SCHEDULE_WITHOUT_REPEAT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "ㅇㅇ";
        String scheduleContent = "ㅇ";
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

        String scheduleTitle = "adf";
        String scheduleContent = "ff";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(wrongMonthStartedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid date format detected Schedule start date \""+scheduleRequest.getStartedAt()+"\" Schedule finish date \""+scheduleRequest.getFinishAt()+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(wrongYearStartedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();
        int startYear = 1999;
        int finishYear = 2023;

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Year cannot be under 2000 current Schedule start year \""+startYear+"\" finish year \""+finishYear+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(wrongCycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cycle cannot be less than or equal to zero, current cycle \""+wrongCycle+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(wrongRepeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Repeat finish date \""+wrongRepeatFinishAt+"\" cannot precede schedule start date \""+scheduleRequest.getStartedAt()+"\" or finish date \""+scheduleRequest.getFinishAt()+"\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 잘못된 형식의 반복 종료 날짜")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_INVALID_REPEAT_FINISH_DATE_FORMAT_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String wrongRepeatFinishAt = "2023-13-28T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(wrongRepeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid date format detected Schedule repeat finish date \"" + wrongRepeatFinishAt + "\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(blankScheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-407"),
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
        String startedAt = "2023-01-31T23:59";
        String finishAt = "2023-01-24T20:52";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule finish date \"" + finishAt + "\" cannot precede start date \"" + startedAt + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("새로운 일정 생성 실패 테스트 - 권한 없음")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String Name = "object";
        String UserId = "id123";
        String UserPassword = "objectwd123!!";
        String PhoneNumber = "010-1234-2222";
        UserRequest UserCreateRequest = UserRequest.builder()
                .name(Name)
                .userId(UserId)
                .userPassword(UserPassword)
                .phoneNumber(PhoneNumber)
                .build();

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        String wrongToken = createUserAndGetToken(UserCreateRequest);
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, wrongToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule length content cannot be more than 1000 current length \"" + scheduleContent.length() + "\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.repeat.repeat_finish_at").value(scheduleRequest.getRepeat().getRepeatFinishAt())
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("특정 일정 조회 실패 테스트 - 없는 schedule id")
    public void INQUIRY_SPECIFIC_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST() throws Exception{
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find schedule with id \"" + id + "\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(startedAt3)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.schedules.[0].schedule_title").value(scheduleRequest.getScheduleTitle()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].started_at").value(scheduleRequest.getStartedAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].finish_at").value(scheduleRequest.getFinishAt()),
                MockMvcResultMatchers.jsonPath("$.schedules.[0].repeat").isEmpty(),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(StartedAt3)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Month cannot be under 1 or over 12 current month \""+wrongMonth+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest3 = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle3)
                .scheduleContent(scheduleContent3)
                .startedAt(startedAt3)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-400"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Year cannot be under 2000 current year \"" + wrongYear + "\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(wrongModifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid date format detected Schedule start date \""+modifiedScheduleRequest.getStartedAt()+"\" Schedule finish date \""+modifiedScheduleRequest.getFinishAt()+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(wrongModifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), adminToken, objectMapper.writeValueAsString(modifiedScheduleRequest));
        int startedYear = 1999;
        int finishYear = 2023;


        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Year cannot be under 2000 current Schedule start year \""+startedYear+"\" finish year \""+finishYear+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(wrongModifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc,adminToken,objectMapper.writeValueAsString(scheduleRequest));
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cycle cannot be less than or equal to zero, current cycle \""+wrongModifiedCycle+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(wrongModifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Repeat finish date \""+wrongModifiedRepeatFinishAt+"\" cannot precede schedule start date \""+modifiedScheduleRequest.getStartedAt()+"\" or finish date \""+scheduleRequest.getFinishAt()+"\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(wrongModifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-407"),
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
        String modifiedStartedAt = "2023-01-31T23:59";
        String modifiedfinishAt = "2023-01-24T20:52";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule finish date \"" + modifiedfinishAt + "\" cannot precede start date \"" + modifiedStartedAt + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 권한 없음")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String Name = "object";
        String UserId = "id123";
        String UserPassword = "objectwd123!!";
        String PhoneNumber = "010-1234-2222";
        UserRequest UserCreateRequest = UserRequest.builder()
                .name(Name)
                .userId(UserId)
                .userPassword(UserPassword)
                .phoneNumber(PhoneNumber)
                .build();

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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        String wrongToken = createUserAndGetToken(UserCreateRequest);
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), wrongToken, objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(wrongModifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Schedule length content cannot be more than 1000 current length \"" + wrongModifiedScheduleContent.length() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 수정 실패 테스트 - 없는 schedule id")
    public void MODIFY_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = 0;
        String modifiedScheduleTitle = "seminar";
        String modifiedScheduleContent = "DFS";
        String modifiedStartedAt = "2023-01-24T20:52";
        String modifiedfinishAt = "2023-01-31T23:59";
        int modifiedCycle = 7;
        String modifiedRepeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(modifiedCycle)
                .repeatFinishAt(modifiedRepeatFinishAt)
                .build();
        ScheduleRequest modifiedScheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(modifiedScheduleTitle)
                .scheduleContent(modifiedScheduleContent)
                .startedAt(modifiedStartedAt)
                .finishAt(modifiedfinishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.modifySchedule(mvc, id, adminToken,objectMapper.writeValueAsString(modifiedScheduleRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find schedule with id \"" + id + "\""),
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
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
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
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 삭제 실패 테스트 - 권한 없음")
    public void DELETE_SCHEDULE_FAIL_CAUSE_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String Name = "object";
        String UserId = "id123";
        String UserPassword = "objectwd123!!";
        String PhoneNumber = "010-1234-2222";
        UserRequest UserCreateRequest = UserRequest.builder()
                .name(Name)
                .userId(UserId)
                .userPassword(UserPassword)
                .phoneNumber(PhoneNumber)
                .build();

        String scheduleTitle = "seminar";
        String scheduleContent = "BFS";
        String startedAt = "2023-01-24T20:52";
        String finishAt = "2023-01-31T23:59";
        int cycle = 123;
        String repeatFinishAt = "2023-12-31T23:59";
        ScheduleRepeatRequest scheduleRepeatRequest = ScheduleRepeatRequest.builder()
                .cycle(cycle)
                .repeatFinishAt(repeatFinishAt)
                .build();
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .scheduleTitle(scheduleTitle)
                .scheduleContent(scheduleContent)
                .startedAt(startedAt)
                .finishAt(finishAt)
                .repeat(scheduleRepeatRequest)
                .build();

        //when
        String wrongToken = createUserAndGetToken(UserCreateRequest);
        ScheduleAcceptanceTestHelper.createNewSchedule(mvc, adminToken, objectMapper.writeValueAsString(scheduleRequest));
        ScheduleListResponse scheduleListResponse = objectMapper.readValue(
                ScheduleAcceptanceTestHelper.inquiryScheduleListByTerm(mvc, 2023, 1, adminToken)
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), ScheduleListResponse.class);
        ResultActions result = ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, scheduleListResponse.getScheduleList().get(0).getId(), wrongToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        ).andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("일정 삭제 실패 테스트 - 없는 schedule id")
    public void DELETE_SCHEDULE_FAIL_CAUSE_UNKNOWN_SCHEDULE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = 0;

        //when
        ResultActions result = ScheduleAcceptanceTestHelper.deleteSpecificSchedule(mvc, id, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("SCHEDULE-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Cannot find schedule with id \"" + id + "\""),
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

    private String createUserAndGetToken(UserRequest userRequest) throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        UserAcceptanceTestHelper.approveJoinRequest(mvc,adminToken,userRequest.getUserId());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userRequest.getUserId())
                .userPassword(userRequest.getUserPassword())
                .build());
    }

}
