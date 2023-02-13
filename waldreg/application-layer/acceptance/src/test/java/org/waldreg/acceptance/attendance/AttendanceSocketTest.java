package org.waldreg.acceptance.attendance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AttendanceSocketTest{

    @Test
    @DisplayName("출석 번호, 출석 시간 구독 성공 테스트")
    void SUBSCRIBE_KEY_AND_TIME_SUCCESS_TEST() {
        Assertions.assertDoesNotThrow(AttendanceStompClient::getClient);
    }

    @Test
    @DisplayName("출석 시작 성공 테스트")
    void START_ATTENDANCE_SUCCESS_TEST() {
        // given
        AttendanceStompClient attendanceStompClient = AttendanceStompClient.getClient();

        // when & then
        Assertions.assertDoesNotThrow(attendanceStompClient::startAttendance);
        attendanceStompClient.stopAttendance();
    }

    @Test
    @DisplayName("출석 시작 및 출석 시간 받기 테스트")
    void START_ATTENDANCE_AND_RECEIVE_ATTENDANCE_TIME_SUCCESS_TEST(){
        // given
        AttendanceStompClient attendanceStompClient = AttendanceStompClient.getClient();

        // when
        attendanceStompClient.startAttendance();

        // then
        Assertions.assertInstanceOf(String.class, attendanceStompClient.getAttendanceTime());
        attendanceStompClient.stopAttendance();
    }

    @Test
    @DisplayName("출석 시작 및 출석 번호 받기 테스트")
    void START_ATTENDANCE_AND_RECEIVE_ATTENDANCE_NUMBER_SUCCESS_TEST(){
        // given
        AttendanceStompClient attendanceStompClient = AttendanceStompClient.getClient();

        // when
        attendanceStompClient.startAttendance();

        // then
        Assertions.assertInstanceOf(String.class, attendanceStompClient.getAttendanceNumber());
        attendanceStompClient.stopAttendance();
    }

    @Test
    @DisplayName("출석 종료 성공 테스트")
    void STOP_ATTENDANCE_SUCCESS_SUCCESS_TEST(){
        // given
        AttendanceStompClient attendanceStompClient = AttendanceStompClient.getClient();

        // when
        attendanceStompClient.startAttendance();

        // then
        Assertions.assertDoesNotThrow(attendanceStompClient::stopAttendance);
        attendanceStompClient.stopAttendance();
    }

}
