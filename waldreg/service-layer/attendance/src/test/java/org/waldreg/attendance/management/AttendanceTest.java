package org.waldreg.attendance.management;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import org.waldreg.attendance.exception.InvalidDateException;
import org.waldreg.attendance.exception.TooEarlyDateException;
import org.waldreg.attendance.exception.TooFarDateException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.spi.AttendanceRepository;
import org.waldreg.attendance.management.spi.UserExistChecker;
import org.waldreg.attendance.rule.AttendanceRule;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultAttendanceManager.class, AttendanceDateValidator.class})
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
        Mockito.when(attendanceRepository.readAttendanceTarget(id)).thenReturn(Optional.of(attendanceTargetDto));
        AttendanceTargetDto result = attendanceManager.readAttendanceTarget(id);

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
        Mockito.when(attendanceRepository.readAttendanceTarget(id)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(DoesNotRegisteredAttendanceException.class, () -> attendanceManager.readAttendanceTarget(id));
    }

    @Test
    @DisplayName("출석 상태 강제 변경 성공 테스트")
    void CHANGE_ATTENDANCE_STATUS_FORCE_SUCCESS_TEST(){
        // given
        AttendanceStatusChangeDto request = AttendanceStatusChangeDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .attendanceDate(LocalDate.now().plusDays(1))
                .build();

        AttendanceTargetDto attendanceTargetDto = AttendanceTargetDto.builder()
                .id(1)
                .userId("hello world")
                .attendanceStatus(AttendanceType.ABSENCE)
                .build();

        // when
        Mockito.when(attendanceRepository.readAttendanceTarget(request.getId())).thenReturn(Optional.of(attendanceTargetDto));
        Mockito.when(userExistChecker.isExistUser(request.getId())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> attendanceManager.changeAttendanceStatus(request));
    }

    @Test
    @DisplayName("출석 상태 강제 변경 실패 테스트 - 출석 대상이 아님")
    void CHANGE_ATTENDANCE_STATUS_FORCE_FAIL_NOT_ATTENDANCE_TARGET_TEST(){
        // given
        AttendanceStatusChangeDto request = AttendanceStatusChangeDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .attendanceDate(LocalDate.now().plusDays(1))
                .build();

        // when
        Mockito.when(attendanceRepository.readAttendanceTarget(request.getId())).thenReturn(Optional.empty());
        Mockito.when(userExistChecker.isExistUser(request.getId())).thenReturn(true);

        // then
        Assertions.assertThrows(DoesNotRegisteredAttendanceException.class, () -> attendanceManager.changeAttendanceStatus(request));
    }

    @Test
    @DisplayName("출석 상태 강제 변경 실패 테스트 - 유저를 찾을 수 없음")
    void CHANGE_ATTENDANCE_STATUS_FORCE_FAIL_CANNOT_FIND_USER_TEST(){
        // given
        AttendanceStatusChangeDto request = AttendanceStatusChangeDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .attendanceDate(LocalDate.now().plusDays(1))
                .build();

        // when
        Mockito.when(attendanceRepository.readAttendanceTarget(request.getId())).thenReturn(Optional.empty());
        Mockito.when(userExistChecker.isExistUser(request.getId())).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownUsersIdException.class, () -> attendanceManager.changeAttendanceStatus(request));
    }

    @Test
    @DisplayName("출석 상태 강제 변경 실패 테스트 - 날찌가 너무 빠름")
    void CHANGE_ATTENDANCE_STATUS_FORCE_FAIL_TOO_EARLY_DATE_TEST(){
        // given
        AttendanceStatusChangeDto request = AttendanceStatusChangeDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .attendanceDate(LocalDate.now().minusDays(AttendanceRule.ATTENDANCE_SAVED_DATE + 1))
                .build();

        AttendanceTargetDto attendanceTargetDto = AttendanceTargetDto.builder()
                .id(1)
                .userId("hello world")
                .attendanceStatus(AttendanceType.ABSENCE)
                .build();

        // when
        Mockito.when(attendanceRepository.readAttendanceTarget(request.getId())).thenReturn(Optional.of(attendanceTargetDto));
        Mockito.when(userExistChecker.isExistUser(request.getId())).thenReturn(true);

        // then
        Assertions.assertThrows(TooEarlyDateException.class, () -> attendanceManager.changeAttendanceStatus(request));
    }

    @Test
    @DisplayName("출석 상태 강제 변경 실패 테스트 - 날찌가 너무 멈")
    void CHANGE_ATTENDANCE_STATUS_FORCE_FAIL_TOO_FAR_DATE_TEST(){
        // given
        AttendanceStatusChangeDto request = AttendanceStatusChangeDto.builder()
                .id(1)
                .attendanceType(AttendanceType.ATTENDANCED)
                .attendanceDate(LocalDate.now().plusDays(AttendanceRule.ATTENDANCE_SAVED_DATE + 1))
                .build();

        AttendanceTargetDto attendanceTargetDto = AttendanceTargetDto.builder()
                .id(1)
                .userId("hello world")
                .attendanceStatus(AttendanceType.ABSENCE)
                .build();

        // when
        Mockito.when(attendanceRepository.readAttendanceTarget(request.getId())).thenReturn(Optional.of(attendanceTargetDto));
        Mockito.when(userExistChecker.isExistUser(request.getId())).thenReturn(true);

        // then
        Assertions.assertThrows(TooFarDateException.class, () -> attendanceManager.changeAttendanceStatus(request));
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 출석 현황 조회 성공 테스트")
    void READ_ALL_ATTENDANCE_TARGET_USERS_ATTENDANCE_STATUS_SUCCESS_TEST(){
        // given
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(59);
        List<AttendanceDayDto> attendanceDayDtoList = getAttendanceDayDtoList(from, to);

        // when
        Mockito.when(attendanceRepository.readAttendanceStatusList(from, to))
                .thenReturn(attendanceDayDtoList);
        List<AttendanceDayDto> result = attendanceManager.readAttendanceStatusList(from, to);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(60, result.size()),
                () -> Assertions.assertTrue(isDateAllMatch(from, to, result))
        );
    }

    private List<AttendanceDayDto> getAttendanceDayDtoList(LocalDate from, LocalDate to){
        LocalDate idx = from;
        List<AttendanceDayDto> attendanceDayDtoList = new ArrayList<>();
        while(!idx.isAfter(to)){
            attendanceDayDtoList.add(
                    AttendanceDayDto.builder()
                            .attendanceDate(idx)
                            .attendanceUserList(List.of())
                            .build()
            );
            idx = idx.plusDays(1);
        }
        return attendanceDayDtoList;
    }

    private boolean isDateAllMatch(LocalDate from, LocalDate to, List<AttendanceDayDto> attendanceDayDtoList){
        LocalDate idx = from;
        int listIdx = 0;
        while(!idx.isAfter(to)){
            if(!attendanceDayDtoList.get(listIdx)
                    .getAttendanceDate().equals(idx)){
                return false;
            }
            idx = idx.plusDays(1);
            listIdx++;
        }
        return true;
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 출석 현황 조회 실패 테스트 - Date가 너무 이름")
    void READ_ALL_ATTENDANCE_TARGET_USERS_ATTENDANCE_STATUS_FAIL_TOO_EARLY_DATE_TEST(){
        // given
        LocalDate from = LocalDate.now().minusDays(AttendanceRule.ATTENDANCE_SAVED_DATE+1);
        LocalDate to = LocalDate.now();

        // when & then
        Assertions.assertThrows(TooEarlyDateException.class, () -> attendanceManager.readAttendanceStatusList(from, to));
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 출석 현황 조회 실패 테스트 - Date가 너무 멈")
    void READ_ALL_ATTENDANCE_TARGET_USERS_ATTENDANCE_STATUS_FAIL_TOO_FAR_DATE_TEST(){
        // given
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(AttendanceRule.ATTENDANCE_SAVED_DATE+1);

        // when & then
        Assertions.assertThrows(TooFarDateException.class, () -> attendanceManager.readAttendanceStatusList(from, to));
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 출석 현황 조회 실패 테스트 - From이 To보다 뒤에 있음")
    void READ_ALL_ATTENDANCE_TARGET_USERS_ATTENDANCE_STATUS_FAIL_FROM_AFTER_THAN_TO_TEST(){
        // given
        LocalDate from = LocalDate.now().plusDays(10);
        LocalDate to = LocalDate.now();

        // when & then
        Assertions.assertThrows(InvalidDateException.class, () -> attendanceManager.readAttendanceStatusList(from, to));
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 출석 현황 조회 실패 테스트 - From과 To가 너무 멀리 있음")
    void READ_ALL_ATTENDANCE_TARGET_USERS_ATTENDANCE_STATUS_FAIL_FAR_BETWEEN_FROM_AND_TO_TEST(){
        // given
        LocalDate from = LocalDate.now().minusDays(50);
        LocalDate to = LocalDate.now().plusDays(50);

        // when & then
        Assertions.assertThrows(InvalidDateException.class, () -> attendanceManager.readAttendanceStatusList(from, to));
    }

}
