package org.waldreg.attendance.management;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.attendance.type.AttendanceType;

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

    @Test
    @DisplayName("출석 대상에서 제거 성공 테스트")
    void DELETE_REGISTERED_ATTENDANCE_TARGET_SUCCESS_TEST(){
        // given
        int id = 1;

        // when
        Mockito.when(userExistChecker.isExistUser(id)).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> attendanceManager.deleteRegisteredAttendanceTarget(id));
    }

    @Test
    @DisplayName("출석 대상에서 제거 실패 테스트 - id에 해당하는 user를 찾을 수 없음")
    void DELETE_REGISTERED_ATTENDANCE_TARGET_FAIL_CANNOT_FIND_USER_BY_ID(){
        // given
        int id = 1;

        // when
        Mockito.when(userExistChecker.isExistUser(id)).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownUsersIdException.class,
                () -> attendanceManager.deleteRegisteredAttendanceTarget(id));
    }

    @Test
    @DisplayName("출석 대상인지 확인 성공 테스트")
    void CHECK_ATTENDANCE_TARGET_SUCCESS_TEST(){
        // given
        int id = 1;
        AttendanceTargetDto attendanceTargetDto = AttendanceTargetDto.builder()
                .id(id)
                .userId("hello world")
                .attendanceStatus(AttendanceType.ABSENCE)
                .build();

        // when
        Mockito.when(attendanceRepository.getAttendanceTarget(id)).thenReturn(Optional.of(attendanceTargetDto));
        AttendanceTargetDto result = attendanceManager.getAttendanceTarget(id);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(attendanceTargetDto.getId(), result.getId()),
                () -> Assertions.assertEquals(attendanceTargetDto.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(attendanceTargetDto.getAttendanceStatus(), result.getAttendanceStatus())
        );
    }

    @Test
    @DisplayName("출석 대상인지 확인 실패 테스트 - 출석 대상이 아님")
    void CHECK_ATTENDANCE_TARGET_FAIL_NOT_REGISTERED_ATTENDANCE_TEST(){
        // given
        int id = 1;

        // when
        Mockito.when(attendanceRepository.getAttendanceTarget(id)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(DoesNotRegisteredAttendanceException.class, () -> attendanceManager.getAttendanceTarget(id));
    }

}
