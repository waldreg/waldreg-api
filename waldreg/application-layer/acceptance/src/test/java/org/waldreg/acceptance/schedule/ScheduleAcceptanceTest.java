package org.waldreg.acceptance.schedule;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        );


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
        );

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
        String StartedAt = "2023-01-24T20:52";
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
                .startedAt(StartedAt)
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
        String StartedAt = "2023-01-24T20:52";
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
                .startedAt(StartedAt)
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
    @DisplayName("새로운 일정 생성 실패 테스트 - 빈 schedule test")
    public void CREATE_NEW_SCHEDULE_FAIL_CAUSE_BLANK_SCHEDULE_TITLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String blankScheduleTitle = "";
        String scheduleContent = "BFS";
        String StartedAt = "2023-01-24T20:52";
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
                .startedAt(StartedAt)
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
        String StartedAt = "2023-01-24T20:52";
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
                .finishAt(StartedAt)
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
        String StartedAt = "2023-01-24T20:52";
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
                .startedAt(StartedAt)
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

        String blankScheduleTitle = "";
        String scheduleContent = createOverflow();
        String StartedAt = "2023-01-24T20:52";
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
                .startedAt(StartedAt)
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

}
