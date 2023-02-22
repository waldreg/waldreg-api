package org.waldreg.acceptance.attendance.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.waldreg.acceptance.attendance.AttendanceAcceptanceTestHelper;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.controller.user.response.UserResponse;

import static org.waldreg.acceptance.attendance.AttendanceAcceptanceValidator.*;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class AttendanceSocketTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    @AfterEach
    void INIT_ATTENDANCE_TARGET() throws Exception{
        int id = getUsersId("Admin");
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        AttendanceAcceptanceTestHelper.deleteSubscribedAttendance(mvc, adminToken, ""+id);
    }

    @Test
    @DisplayName("출석 체크 요청 성공 인수 테스트")
    void CONFIRM_ATTENDANCE_SUCCESS_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceStompClient stompClient = AttendanceStompClient.getClient(id, adminToken);
        stompClient.startAttendance();

        // when
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(stompClient::isUpdated);
        String attendanceNumber = stompClient.getAttendanceNumber();
        Map<String, String> request = Map.of("attendance_identify", attendanceNumber);
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));

        // then
        expectedIsOk(resultActions);
        stompClient.stopAttendance();
    }

    @Test
    @DisplayName("출석 체크 요청 실패 인수 테스트 - 출석 대상이 아닐경우")
    void CONFIRM_ATTENDANCE_FAIL_NOT_SUBSCRIBED_ATTENDANCE_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceStompClient stompClient = AttendanceStompClient.getClient(id, adminToken);
        stompClient.startAttendance();

        // when
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(stompClient::isUpdated);
        String attendanceNumber = stompClient.getAttendanceNumber();
        Map<String, String> request = Map.of("attendance_identify", attendanceNumber);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));

        // then
        expectedIsNotRegisteredOnAttendance(resultActions);
        stompClient.stopAttendance();
    }

    @Test
    @DisplayName("출석 체크 요청 실패 인수 테스트 - 이미 출석한 경우")
    void CONFIRM_ATTENDANCE_FAIL_ALREADY_ATTENDANCED_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceStompClient stompClient = AttendanceStompClient.getClient(id, adminToken);
        stompClient.startAttendance();

        // when
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(stompClient::isUpdated);
        String attendanceNumber = stompClient.getAttendanceNumber();
        Map<String, String> request = Map.of("attendance_identify", attendanceNumber);
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));
        ResultActions resultActions = AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));

        // then
        expectedIsAlreadyAttendanced(resultActions);
    }

    @Test
    @DisplayName("출석 체크 요청 실패 인수 테스트 - 틀린 attendanceNumber")
    void CONFIRM_ATTENDANCE_FAIL_WRONG_NUMBER_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceStompClient stompClient = AttendanceStompClient.getClient(id, adminToken);
        stompClient.startAttendance();

        // when
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(stompClient::isUpdated);
        Map<String, String> request = Map.of("attendance_identify", "hello world");
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));

        // then
        expectedIsWrongNumber(resultActions, "hello world");
    }

    @Test
    @DisplayName("출석 체크 요청 실패 인수 테스트 - 출석을 시작하지 않음")
    void CONFIRM_ATTENDANCE_FAIL_DOES_NOT_STARTED_TEST() throws Exception{
        // given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        int id = getUsersId("Admin");
        AttendanceStompClient stompClient = AttendanceStompClient.getClient(id, adminToken);
        stompClient.stopAttendance();
        Map<String, String> request = Map.of("attendance_identify", "123");

        // when
        AttendanceAcceptanceTestHelper.subscribeAttendance(mvc, adminToken, "" + id);
        ResultActions resultActions = AttendanceAcceptanceTestHelper.confirmAttendance(mvc, adminToken, objectMapper.writeValueAsString(request));

        // then
        expectedIsNotStartedAttendance(resultActions);
    }

    private int getUsersId(String userId) throws Exception{
        ResultActions resultActions = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, userId);
        UserResponse userResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), UserResponse.class);
        return userResponse.getId();
    }

}
