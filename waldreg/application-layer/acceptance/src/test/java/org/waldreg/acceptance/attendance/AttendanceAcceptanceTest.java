package org.waldreg.acceptance.attendance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import org.waldreg.acceptance.reward.RewardAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.reward.tag.response.RewardTagResponse;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class AttendanceAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

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

    @BeforeEach
    @AfterEach
    public void INITIATE_REWARD_TAG() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ArrayList<RewardTagResponse> rewardTagResponseList = new ArrayList<>(
                getRewardTagResponseMap(RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken)).get("reward_tags")
        );
        for(RewardTagResponse rewardTagResponse : rewardTagResponseList){
            RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId());
            RewardAcceptanceTestHelper.deleteRewardTag(mvc, adminToken, rewardTagResponse.getRewardTagId())
                    .andExpectAll(
                            MockMvcResultMatchers.status().isBadRequest(),
                            MockMvcResultMatchers.header().string("Api-version", apiVersion),
                            MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                            MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                            MockMvcResultMatchers.jsonPath("$.code").value("REWARD-410"),
                            MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id \"" + rewardTagResponse.getRewardTagId() + "\""),
                            MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
                    );
        }
        rewardTagResponseList.clear();
    }

    private Map<String, List<RewardTagResponse>> getRewardTagResponseMap(ResultActions resultActions) throws Exception{
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>(){});
    }

    @Test
    @DisplayName("출석 대상 등록 성공 인수 테스트")
    void SUBSCRIBE_ATTENDANCE_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String adminUserId = "Admin";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, adminUserId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("출석 대상 등록 실패 인수 테스트 - 권한 없음")
    void SUBSCRIBE_ATTENDANCE_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String userId = "hello world";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, token, userId);

        // then
        expectedIsNoPermission(result);
    }

    @Test
    @DisplayName("출석 대상 등록 성공 인수 테스트 - 여러명 등록")
    void SUBSCRIBE_ATTENDANCE_SUCCESS_MULTIPLE_USER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String multipleUserId = "hello world, Admin";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, multipleUserId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("출석 대상 등록 실패 인수 테스트 - 유저를 찾을 수 없음")
    void SUBSCRIBE_ATTENDANCE_FAIL_CANNOT_FIND_USER_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "someone";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, userId);

        // then
        expectedIsUnknownUserId(result, userId);
    }

    @Test
    @DisplayName("출석 대상 제거 성공 인수 테스트")
    void DELETE_SUBSCRIBED_ATTENDANCE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, userId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("출석 대상 제거 성공 인수 테스트 - 여러명 삭제")
    void DELETE_SUBSCRIBED_ATTENDANCE_SUCCESS_MULTIPLE_DELETE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String multipleUserId = "hello world, Admin";

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, multipleUserId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("출석 대상 제거 실패 인수 테스트 - 권한 없음")
    void DELETE_SUBSCRIBED_ATTENDANCE_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String userId = "hello world";

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, token, userId);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("출석 대상 제거 실패 인수 테스트 - 유저를 찾을 수 없음")
    void DELETE_SUBSCRIBED_ATTENDANCE_FAIL_CANNOT_FIND_USER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "someone";

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, userId);

        // then
        expectedIsUnknownUserId(resultActions, userId);
    }

    @Test
    @DisplayName("자신이 출석 대상인지 확인 성공 인수 테스트")
    void AM_I_ATTENDANCE_TARGET_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, userId);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.amIAttendanceTarget(mvc, adminToken);

        // then
        expectedIsOk(resultActions);
        resultActions.andExpectAll(
                MockMvcResultMatchers.jsonPath("$.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.attendance_required").value(true),
                MockMvcResultMatchers.jsonPath("$.attendance_status").value(AttendanceType.ABSENCE.toString())
        );
    }

    @Test
    @DisplayName("자신이 출석 대상인지 확인 실패 인수 테스트 - 출석 대상에 등록 되지 않음")
    void AM_ID_ATTENDANCE_TARGET_FAIL_DOES_NOT_SUBSCRIBED_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.amIAttendanceTarget(mvc, adminToken);

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
    }

    @Test
    @DisplayName("출석 면제 신청 성공 인수 테스트")
    void WAIVER_ATTENDANCE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now())
                .waiverReason("play game")
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "hello world");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.waiver(mvc, token, objectMapper.writeValueAsString(attendanceWaiverRequest));

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("출석 면제 신청 실패 인수 테스트 - 출석 대상이 아님")
    void WAIVER_ATTENDANCE_FAIL_DOES_NOT_REGISTERED_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now())
                .waiverReason("play game")
                .build();

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.waiver(mvc, token, objectMapper.writeValueAsString(attendanceWaiverRequest));

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
    }

    @Test
    @DisplayName("출석 면제 신청 실패 인수 테스트 - 서버의 현재 날짜보다 이전 날짜로 면제 신청을 한 경우")
    void WAIVER_ATTENDANCE_FAIL_BEFORE_DATE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now().minusDays(1))
                .waiverReason("play game")
                .build();

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.waiver(mvc, token, objectMapper.writeValueAsString(attendanceWaiverRequest));

        // then
        expectedIsInvalidWaiverDate(resultActions);
    }

    @Test
    @DisplayName("출석 면제 신청 실패 인수 테스트 - 서버의 현재 날짜보다 100일 이상인 지점에 면제 신청을 할 경우 발생함")
    void WAIVER_ATTENDANCE_FAIL_WAIVE_AFTER_100DAYS_TEST() throws Exception{
        // given
        LocalDate date = LocalDate.now().plusDays(100);
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(date)
                .waiverReason("play game")
                .build();

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.waiver(mvc, token, objectMapper.writeValueAsString(attendanceWaiverRequest));

        // then
        expectedIsTooFarDate(resultActions, date);
    }

    @Test
    @DisplayName("출석 면제 신청 목록 조회 성공 인수 테스트")
    void READ_WAIVER_LIST_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        LocalDate date = LocalDate.now().plusDays(1);
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(date)
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, adminToken);

        // then
        expectedIsOk(resultActions);
        resultActions.andExpectAll(
                MockMvcResultMatchers.jsonPath("$.waivers.[0].waiver_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].user_name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].wavier_date").value(date.toString()),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].waiver_reason").value("doctor's appointment")
        );
    }

    @Test
    @DisplayName("출석 면제 신청 목록 조회 실패 인수 테스트 - 권한 없음")
    void READ_WAIVER_LIST_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, token);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("출석 면제 승인 및 출석 상태 변경 성공 인수 테스트")
    void ACCEPT_WAIVER_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        AttendanceWaiverResponse attendanceWaiverResponse = getAttendanceWaiverResponseList().get(0);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, attendanceWaiverResponse.getWaiverId(), AttendanceType.ACKNOWLEDGE_ABSENCE);

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("출석 면제 승인 및 출석 상태 변경 실패 인수 테스트 - 권한 없음")
    void ACCEPT_WAIVER_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, token, 1, AttendanceType.ABSENCE);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("출석 면제 승인 및 출석 상태 변경 실패 인수 테스트 - 없는 waiver-id")
    void ACCEPT_WAIVER_FAIL_UNKNOWN_WAIVER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, 1, AttendanceType.LATE_ATTENDANCE);

        // then
        expectedIsUnknownWaiverId(resultActions, 1);
    }

    @Test
    @DisplayName("출석 면제 승인 및 출석 상태 변경 실패 인수 테스트 - 없는 attendance-type")
    void ACCEPT_WAIVER_FAIL_UNKNOWN_ATTENDANCE_TYPE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        AttendanceWaiverResponse attendanceWaiverResponse = getAttendanceWaiverResponseList().get(0);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, attendanceWaiverResponse.getWaiverId(), "UNKNOWN");

        // then
        expectedIsUnknownAttendanceType(resultActions, "UNKNOWN");
    }

    private void expectedIsOk(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );
    }

    private void expectedIsNoPermission(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsUnknownUserId(ResultActions resultActions, String userId) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-413"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id \"" + userId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsNotRegisteredOnAttendance(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Not registered attendance list"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsInvalidWaiverDate(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-416"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid waiver date"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsTooFarDate(ResultActions resultActions, LocalDate date) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-415"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Date is too far \"" + date.toString() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsUnknownWaiverId(ResultActions resultActions, int waiverId) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-411"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown waiver id \"" + waiverId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private void expectedIsUnknownAttendanceType(ResultActions resultActions, String attendanceType) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown attendance type \"" + attendanceType + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

    public List<AttendanceWaiverResponse> getAttendanceWaiverResponseList() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, adminToken);
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, List<AttendanceWaiverResponse>> ans =  objectMapper.readValue(content, new TypeReference<>(){});
        return ans.get("waivers");
    }

    public static final class AttendanceWaiverRequest{

        @JsonProperty("waiver_date")
        private LocalDate waiverDate;
        @JsonProperty("waiver_reason")
        private String waiverReason;

        public AttendanceWaiverRequest(){}

        private AttendanceWaiverRequest(Builder builder){
            this.waiverDate = builder.waiverDate;
            this.waiverReason = builder.waiverReason;
        }

        public static Builder builder(){
            return new Builder();
        }

        public LocalDate getWaiverDate(){
            return waiverDate;
        }

        public String getWaiverReason(){
            return waiverReason;
        }

        public static final class Builder{

            private LocalDate waiverDate;
            private String waiverReason;

            private Builder(){}

            public Builder waiverDate(LocalDate waiverDate){
                this.waiverDate = waiverDate;
                return this;
            }

            public Builder waiverReason(String waiverReason){
                this.waiverReason = waiverReason;
                return this;
            }

            public AttendanceWaiverRequest build(){
                return new AttendanceWaiverRequest(this);
            }

        }

    }

    public static final class AttendanceWaiverResponse{

        private int waiverId;
        private String userId;
        private String userName;
        private LocalDate waiverDate;
        private String waiverReason;

        public AttendanceWaiverResponse(){}

        private AttendanceWaiverResponse(Builder builder){
            this.waiverId = builder.waiverId;
            this.userId = builder.userId;
            this.userName = builder.userName;
            this.waiverDate = builder.waiverDate;
            this.waiverReason = builder.waiverReason;
        }

        public static Builder builder(){
            return new Builder();
        }

        public int getWaiverId(){
            return waiverId;
        }

        public String getUserId(){
            return userId;
        }

        public String getUserName(){
            return userName;
        }

        public LocalDate getWaiverDate(){
            return waiverDate;
        }

        public String getWaiverReason(){
            return waiverReason;
        }

        public static final class Builder{

            private int waiverId;
            private String userId;
            private String userName;
            private LocalDate waiverDate;
            private String waiverReason;

            private Builder(){}

            public Builder waiverId(int waiverId){
                this.waiverId = waiverId;
                return this;
            }

            public Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public Builder userName(String userName){
                this.userName = userName;
                return this;
            }

            public Builder waiverDate(LocalDate waiverDate){
                this.waiverDate = waiverDate;
                return this;
            }

            public Builder waiverReason(String waiverReason){
                this.waiverReason = waiverReason;
                return this;
            }

            public AttendanceWaiverResponse build(){
                return new AttendanceWaiverResponse(this);
            }

        }

    }

    public enum AttendanceType{

        ATTENDANCED("attendanced"),
        ACKNOWLEDGE_ABSENCE("acknowledge_absence"),
        ABSENCE("absence"),
        LATE_ATTENDANCE("late_attendance");

        private final String name;

        AttendanceType(String name){
            this.name = name;
        }

        @Override
        public String toString(){
            return name;
        }

    }

}
