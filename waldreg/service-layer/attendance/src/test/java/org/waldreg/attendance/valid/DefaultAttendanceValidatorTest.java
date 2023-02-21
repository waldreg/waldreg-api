package org.waldreg.attendance.valid;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.exception.AlreadyAttendanceException;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.DoesNotStartedAttendanceException;
import org.waldreg.attendance.exception.WrongAttendanceIdentifyException;
import org.waldreg.attendance.valid.spi.AttendanceValidatorRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultAttendanceValidator.class})
class DefaultAttendanceValidatorTest{

    @Autowired
    private AttendanceValidator attendanceValidator;

    @MockBean
    private AttendanceTargetValidable attendanceTargetValidable;

    @MockBean
    private AttendanceIdentifyValidable attendanceIdentifyValidable;

    @MockBean
    private AttendanceValidatorRepository attendanceValidatorRepository;

    @Test
    @DisplayName("출석 체크 성공 테스트")
    void CONFIRM_ATTENDANCE_SUCCESS_TEST(){
        // given
        int id = 1;
        String identify = "123";

        // then
        Assertions.assertDoesNotThrow(() -> attendanceValidator.confirm(id, identify));
    }

    @Test
    @DisplayName("출석 체크 실패 테스트 - 출석 대상이 아님")
    void CONFIRM_ATTENDANCE_FAIL_NOT_REGISTERED_ATTENDANCE_LIST_TEST(){
        // given
        int id = 1;
        String identify = "123";

        // when
        Mockito.doThrow(DoesNotRegisteredAttendanceException.class).when(attendanceTargetValidable).throwIfDoesNotNeedAttendance(id);

        // then
        Assertions.assertThrows(DoesNotRegisteredAttendanceException.class, () -> attendanceValidator.confirm(id, identify));
    }

    @Test
    @DisplayName("출석 체크 실패 테스트 - 출석이 필요한 상태가 아님")
    void CONFIRM_ATTENDANCE_FAIL_DOSE_NOT_NEED_ATTENDANCE_TEST(){
        // given
        int id = 1;
        String identify = "123";

        // when
        Mockito.doThrow(AlreadyAttendanceException.class).when(attendanceTargetValidable).throwIfDoesNotNeedAttendance(id);

        // then
        Assertions.assertThrows(AlreadyAttendanceException.class, () -> attendanceValidator.confirm(id, identify));
    }

    @Test
    @DisplayName("출석 체크 실패 테스트 - 출석 번호가 다름")
    void CONFIRM_ATTENDANCE_FAIL_WRONG_IDENTIFY_TEST(){
        // given
        int id = 1;
        String identify = "123";

        // when
        Mockito.doThrow(WrongAttendanceIdentifyException.class).when(attendanceIdentifyValidable).valid(identify);

        // then
        Assertions.assertThrows(WrongAttendanceIdentifyException.class, () -> attendanceValidator.confirm(id, identify));
    }

    @Test
    @DisplayName("출석 체크 실패 테스트 - 출석을 아직 시작하지 않음")
    void CONFIRM_ATTENDANCE_FAIL_DOES_NOT_STARTED_ATTENDANCE_TEST(){
        // given
        int id = 1;
        String identify = "123";

        // when
        Mockito.doThrow(DoesNotStartedAttendanceException.class).when(attendanceIdentifyValidable).valid(identify);

        // then
        Assertions.assertThrows(DoesNotStartedAttendanceException.class, () -> attendanceValidator.confirm(id, identify));
    }

}
