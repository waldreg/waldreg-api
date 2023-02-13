package org.waldreg.attendance.management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DefaultAttendanceManager.class)
class AttendanceTest{

    @Autowired
    private AttendanceManager attendanceManager;

    @MockBean
    private AttendanceRepository attendanceRepository;

    @MockBean
    private UserExistChecker userExistChecker;

    @Test
    @DisplayName("출석 대상 등록 성공 테스트")
    void REGISTER_ATTENDANCE_TARGET_SUCCESS_TEST(){
        // given
        int id = 1;

        // when
        Mockito.when(userExistChecker.isExistUser(id)).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> attendanceManager.registerAttendanceTarget(id));
    }

    @Test
    @DisplayName("출석 대상 등록 실패 테스트 - 유저를 찾을 수 없음")
    void REGISTER_ATTENDANCE_TARGET_FAIL_UNKNOWN_USER_TEST(){
        // given
        int id = 1;

        // when
        Mockito.when(userExistChecker.isExistUser(id)).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownUsersIdException.class, () -> attendanceManager.registerAttendanceTarget(id));
    }

}
