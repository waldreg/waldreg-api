package org.waldreg.acceptance.attendance;

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
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.reward.RewardAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.attendance.management.request.AttendanceModifyRequest;
import org.waldreg.controller.attendance.waiver.request.AttendanceWaiverRequest;
import org.waldreg.controller.attendance.waiver.response.AttendanceWaiverResponse;
import org.waldreg.controller.reward.tag.request.RewardTagRequest;
import org.waldreg.controller.reward.tag.response.RewardTagResponse;
import org.waldreg.controller.joiningpool.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

import static org.waldreg.acceptance.attendance.AttendanceAcceptanceValidator.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class AttendanceAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

    @BeforeEach
    @AfterEach
    void INITIATE_USER() throws Exception{
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
    void INITIATE_REWARD_TAG() throws Exception{
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

    @BeforeEach
    @AfterEach
    void INIT_ATTENDANCE_TARGET() throws Exception{
        int id = getUsersId("Admin");
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, ""+id);
    }

    private Map<String, List<RewardTagResponse>> getRewardTagResponseMap(ResultActions resultActions) throws Exception{
        String content = resultActions.andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(content, new TypeReference<>(){});
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????????")
    void SUBSCRIBE_ATTENDANCE_SUCCESS_ACCEPTANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String adminUserId = "Admin";
        int adminId = getUsersId(adminUserId);

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+adminId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void SUBSCRIBE_ATTENDANCE_FAIL_ACCEPTANCE_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String userId = "hello world";
        int id = getUsersId(userId);

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, token, ""+id);

        // then
        expectedIsNoPermission(result);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ??????")
    void SUBSCRIBE_ATTENDANCE_SUCCESS_MULTIPLE_USER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("hello world");
        int id2 = getUsersId("Admin");
        String multipleUserId = id1 + ", " + id2;

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, multipleUserId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ?????? ??? ??????")
    void SUBSCRIBE_ATTENDANCE_FAIL_CANNOT_FIND_USER_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = 999;

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);

        // then
        expectedIsUnknownUserId(result, id);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????????")
    void DELETE_SUBSCRIBED_ATTENDANCE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";
        int id = getUsersId(userId);

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, "" + id);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ??????")
    void DELETE_SUBSCRIBED_ATTENDANCE_SUCCESS_MULTIPLE_DELETE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("hello world");
        int id2 = getUsersId("Admin");
        String multipleUserId = id1 + ", " + id2;

        // when
        ResultActions result = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, multipleUserId);

        // then
        expectedIsOk(result);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void DELETE_SUBSCRIBED_ATTENDANCE_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String userId = "hello world";
        int id = getUsersId(userId);

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, token, "" + id);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ?????? ??? ??????")
    void DELETE_SUBSCRIBED_ATTENDANCE_FAIL_CANNOT_FIND_USER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = 9999;

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, "" + id);

        // then
        expectedIsUnknownUserId(resultActions, id);
    }

    @Test
    @DisplayName("????????? ?????? ???????????? ?????? ?????? ?????? ?????????")
    void AM_I_ATTENDANCE_TARGET_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";
        int id = getUsersId(userId);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
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
    @DisplayName("????????? ?????? ???????????? ?????? ?????? ?????? ????????? - ?????? ????????? ?????? ?????? ??????")
    void AM_ID_ATTENDANCE_TARGET_FAIL_DOES_NOT_SUBSCRIBED_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.amIAttendanceTarget(mvc, adminToken);

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????????")
    void WAIVER_ATTENDANCE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now())
                .waiverReason("play game")
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ?????? ????????? ??????")
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
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ?????? ???????????? ?????? ????????? ?????? ????????? ??? ??????")
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
    @DisplayName("?????? ?????? ?????? ?????? ?????? ????????? - ????????? ?????? ???????????? 100??? ????????? ????????? ?????? ????????? ??? ?????? ?????????")
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
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ?????? ?????????")
    void READ_WAIVER_LIST_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate date = LocalDate.now().plusDays(1);
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(date)
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, adminToken);

        // then
        expectedIsOk(resultActions);
        resultActions.andExpectAll(
                MockMvcResultMatchers.jsonPath("$.waivers.[0].waiver_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].user_name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].waiver_date").value(date.toString()),
                MockMvcResultMatchers.jsonPath("$.waivers.[0].waiver_reason").value("doctor&#39;s appointment")
        );
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void READ_WAIVER_LIST_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, token);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ?????????")
    void ACCEPT_WAIVER_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        AttendanceWaiverResponse attendanceWaiverResponse = getAttendanceWaiverResponseList().get(0);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, attendanceWaiverResponse.getWaiverId(), AttendanceType.ACKNOWLEDGE_ABSENCE);

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void ACCEPT_WAIVER_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, token, 1, AttendanceType.ABSENCE);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? waiver-id")
    void ACCEPT_WAIVER_FAIL_UNKNOWN_WAIVER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, 1000, AttendanceType.LATE_ATTENDANCE);

        // then
        expectedIsUnknownWaiverId(resultActions, 1000);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ??? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? attendance-type")
    void ACCEPT_WAIVER_FAIL_UNKNOWN_ATTENDANCE_TYPE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceWaiverRequest attendanceWaiverRequest = AttendanceWaiverRequest.builder()
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("doctor's appointment")
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        AttendanceAcceptanceTestHelper.waiver(mvc, adminToken, objectMapper.writeValueAsString(attendanceWaiverRequest));
        AttendanceWaiverResponse attendanceWaiverResponse = getAttendanceWaiverResponseList().get(0);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.acceptWaiver(mvc, adminToken, attendanceWaiverResponse.getWaiverId(), "UNKNOWN");

        // then
        expectedIsUnknownAttendanceType(resultActions, "UNKNOWN");
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ?????????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String userId = "Admin";
        int id = getUsersId(userId);
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType(AttendanceType.ACKNOWLEDGE_ABSENCE.toString())
                .attendanceDate(LocalDate.now())
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? ????????? ??????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType(AttendanceType.ACKNOWLEDGE_ABSENCE.toString())
                .attendanceDate(LocalDate.now())
                .build();

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - attendance-type??? ?????? ??? ??????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_CANNOT_FIND_ATTENDANCE_TYPE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType("UNKNOWN")
                .attendanceDate(LocalDate.now())
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id + "");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsUnknownAttendanceType(resultActions, "UNKNOWN");
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - user id??? ???????????? user??? ?????? ??? ??????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_UNKNOWN_USER_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(999)
                .attendanceType(AttendanceType.ABSENCE.toString())
                .attendanceDate(LocalDate.now())
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id + "");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsUnknownUserId(resultActions, 999);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - attendance_date??? ????????? ?????? ???????????? 100??? ?????? early ?????? ?????????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_BEFORE_HUNDRED_DAYS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate date = LocalDate.now().minusDays(100);
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType(AttendanceType.ABSENCE.toString())
                .attendanceDate(date)
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id + "");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsTooEarlyDate(resultActions, date);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - attendance_date??? ????????? ?????? ???????????? 100??? ?????? ????????? ?????????.")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_AFTER_HUNDRED_DAYS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate date = LocalDate.now().plusDays(100);
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType(AttendanceType.ABSENCE.toString())
                .attendanceDate(date)
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id + "");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, adminToken, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsTooFarDate(resultActions, date);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void FORCE_MODIFY_ATTENDANCE_TYPE_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id = getUsersId("Admin");
        LocalDate date = LocalDate.now().plusDays(30);
        AttendanceModifyRequest attendanceModifyRequest = AttendanceModifyRequest.builder()
                .id(id)
                .attendanceType("UNKNOWN")
                .attendanceDate(date)
                .build();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id + "");
        ResultActions resultActions = AttendanceAcceptanceTestHelper.modifyAttendance(mvc, token, objectMapper.writeValueAsString(attendanceModifyRequest));

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ?????? ?????????")
    void READ_ALL_USERS_ATTENDANCE_STATUS_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("Admin");
        int id2 = getUsersId("hello world");
        LocalDate from = LocalDate.now().minusMonths(1);
        LocalDate to = LocalDate.now().plusMonths(1);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id1 + ", " + id2);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, adminToken, from, to);

        // then
        expectedIsOk(resultActions);
        resultActions.andExpectAll(
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_date").isString(),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_users.[0].id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_users.[0].user_id").isString(),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_users.[0].user_name").isString(),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_users.[0].attendance_status").value(AttendanceType.ABSENCE.toString())
        );
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ??????????????? - ?????? ??????")
    void READ_ALL_USERS_ATTENDANCE_STATUS_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        LocalDate from = LocalDate.now().minusMonths(1);
        LocalDate to = LocalDate.now().plusMonths(1);

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, token, from, to);

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ??????????????? - (to-from) ??? 100??? ?????? ?????? ??????")
    void READ_ALL_USERS_ATTENDANCE_STATUS_FAIL_HUNDRED_DAYS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("Admin");
        int id2 = getUsersId("hello world");
        LocalDate from = LocalDate.now().minusDays(50);
        LocalDate to = LocalDate.now().plusDays(50);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id1 + ", " + id2);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, adminToken, from, to);

        // then
        expectedIsInvalidDate(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ??????????????? - (from > to)")
    void READ_ALL_USERS_ATTENDANCE_STATUS_FAIL_FROM_LATE_TO_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("Admin");
        int id2 = getUsersId("hello world");
        LocalDate from = LocalDate.now().minusMonths(7);
        LocalDate to = LocalDate.now().plusMonths(7);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id1 + ", " + id2);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, adminToken, to, from);

        // then
        expectedIsInvalidDate(resultActions);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ??????????????? - (to) ??? 100??? ?????? ?????????")
    void READ_ALL_USERS_ATTENDANCE_STATUS_FAIL_TOO_FAR_DATE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("Admin");
        int id2 = getUsersId("hello world");
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusMonths(100);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id1 + ", " + id2);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, adminToken, from, to);

        // then
        expectedIsTooFarDate(resultActions, to);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ????????? ?????? ?????? ?????? ?????? ??????????????? - (from) ??? 100??? ?????? ?????????")
    void READ_ALL_USERS_ATTENDANCE_STATUS_FAIL_TOO_EARLY_DATE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        int id1 = getUsersId("Admin");
        int id2 = getUsersId("hello world");
        LocalDate from = LocalDate.now().minusDays(100);
        LocalDate to = LocalDate.now();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, id1 + ", " + id2);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readAttendanceUsers(mvc, adminToken, from, to);

        // then
        expectedIsTooEarlyDate(resultActions, from);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ???????????????")
    void READ_SELF_ATTENDANCE_STATUS_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, from, to);

        // then
        expectedIsOk(resultActions);
        resultActions.andExpectAll(
                MockMvcResultMatchers.jsonPath("$.id").value(id),
                MockMvcResultMatchers.jsonPath("$.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.user_name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_date").value(from.toString()),
                MockMvcResultMatchers.jsonPath("$.attendances.[0].attendance_status").value(AttendanceType.ABSENCE.toString())
        );
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????????????? - ?????? ????????? ??????")
    void READ_SELF_ATTENDANCE_STATUS_FAIL_NOT_SUBSCRIBED_ATTENDANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now();

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, from, to);

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????????????? - (to-from) ??? 100??? ?????? ?????? ??????")
    void READ_SELF_ATTENDANCE_STATUS_FAIL_HUNDRED_DAYS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now().minusDays(50);
        LocalDate to = LocalDate.now().plusDays(50);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, from, to);

        // then
        expectedIsInvalidDate(resultActions);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????????????? - (from < to)")
    void READ_SELF_ATTENDANCE_STATUS_FAIL_FROM_LATE_TO_DAYS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now().plusDays(7);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, to, from);

        // then
        expectedIsInvalidDate(resultActions);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????????????? - (to)??? 100??? ?????????")
    void READ_SELF_ATTENDANCE_STATUS_FAIL_TOO_LATE_DATE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(100);

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, from, to);

        // then
        expectedIsTooFarDate(resultActions, to);
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? ?????? ??????????????? - (from)??? 100??? ?????????")
    void READ_SELF_ATTENDANCE_STATUS_FAIL_TOO_EARLY_DATE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        LocalDate from = LocalDate.now().minusDays(100);
        LocalDate to = LocalDate.now();

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, ""+id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readSelfAttendance(mvc, adminToken, from, to);

        // then
        expectedIsTooEarlyDate(resultActions, from);
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ?????? ?????? ?????????")
    void SET_REWARD_TAG_ON_ATTENDANCE_TYPE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String rewardTagTitle = "absence";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));
        List<RewardTagResponse> rewardTagResponseList = getRewardTagResponseList();
        ResultActions resultActions = AttendanceAcceptanceTestHelper.setAttendanceTagsReward(mvc, adminToken, AttendanceType.ABSENCE, rewardTagResponseList.get(0).getRewardTagId());

        // then
        expectedIsOk(resultActions);
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ?????? ?????? ????????? - ?????? ??????")
    void SET_REWARD_TAG_ON_ATTENDANCE_TYPE_FAIL_NO_PERMISSION_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");
        String rewardTagTitle = "absence";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));
        List<RewardTagResponse> rewardTagResponseList = getRewardTagResponseList();
        ResultActions resultActions = AttendanceAcceptanceTestHelper.setAttendanceTagsReward(mvc, token, AttendanceType.ABSENCE, rewardTagResponseList.get(0).getRewardTagId());

        // then
        expectedIsNoPermission(resultActions);
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ?????? ?????? ????????? - ?????? ????????? ?????? ??? ??????")
    void SET_REWARD_TAG_ON_ATTENDANCE_TYPE_FAIL_UNKNOWN_ATTENDANCE_TYPE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String rewardTagTitle = "absence";
        int rewardPoint = 10;
        RewardTagRequest rewardTagRequest = RewardTagRequest.builder()
                .rewardTagTitle(rewardTagTitle)
                .rewardPoint(rewardPoint)
                .build();

        // when
        RewardAcceptanceTestHelper.createRewardTag(mvc, adminToken, objectMapper.writeValueAsString(rewardTagRequest));
        List<RewardTagResponse> rewardTagResponseList = getRewardTagResponseList();
        ResultActions resultActions = AttendanceAcceptanceTestHelper.setAttendanceTagsReward(mvc, adminToken, "UNKNOWN", rewardTagResponseList.get(0).getRewardTagId());

        // then
        expectedIsUnknownAttendanceType(resultActions, "UNKNOWN");
    }

    @Test
    @DisplayName("?????? ????????? ????????? ?????? ?????? ?????? ?????? ????????? - reward-tag-id??? ?????? ??? ??????")
    void SET_REWARD_TAG_ON_ATTENDANCE_TYPE_FAIL_CANNOT_FIND_REWARD_TAG_ID_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("hong gil dong", "hello world", "abc1234!!!");

        // when
        ResultActions resultActions = AttendanceAcceptanceTestHelper.setAttendanceTagsReward(mvc, adminToken, AttendanceType.ABSENCE, 999999);

        // then
        expectedIsUnknownRewardTagId(resultActions, 999999);
    }

    private void expectedIsUnknownRewardTagId(ResultActions resultActions, int rewardTagId) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-420"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown reward tag id \"" + rewardTagId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        UserAcceptanceTestHelper.approveJoinRequest(mvc,adminToken,userRequest.getUserId());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

    private int getUsersId(String userId) throws Exception{
        ResultActions resultActions = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userId);
        UserResponse userResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);
        return userResponse.getId();
    }

    public List<AttendanceWaiverResponse> getAttendanceWaiverResponseList() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.readWaiver(mvc, adminToken);
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, List<AttendanceWaiverResponse>> ans =  objectMapper.readValue(content, new TypeReference<>(){});
        return ans.get("waivers");
    }

    public List<RewardTagResponse> getRewardTagResponseList() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        ResultActions resultActions = RewardAcceptanceTestHelper.inquiryRewardTagList(mvc, adminToken);
        String content = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, List<RewardTagResponse>> ans = objectMapper.readValue(content, new TypeReference<>(){});
        return ans.get("reward_tags");
    }

}
