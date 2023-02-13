package org.waldreg.attendance.waiver;

import java.time.LocalDate;
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
import org.waldreg.attendance.exception.InvalidWaiverDateException;
import org.waldreg.attendance.exception.TooFarDateException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownWaiverIdException;
import org.waldreg.attendance.rule.AttendanceRule;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.attendance.waiver.spi.WaiverRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultWaiverManager.class, AttendanceDateValidator.class})
class WaiverTest{

    @Autowired
    private WaiverManager waiverManager;

    @MockBean
    private WaiverRepository waiverRepository;

    @Test
    @DisplayName("새로운 출석 면제 추가 성공 테스트")
    void CREATE_NEW_WAIVER_SUCCESS_TEST(){
        // given
        WaiverDto request = WaiverDto.builder()
                .id(1)
                .waiverDate(LocalDate.now())
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.isAttendanceTarget(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> waiverManager.waive(request));
    }

    @Test
    @DisplayName("새로운 출석 면제 추가 실패 테스트 - 출석 대상이 아님")
    void CREATE_NEW_WAIVER_FAIL_NOT_ATTENDANCE_TARGET_TEST(){
        // given
        WaiverDto request = WaiverDto.builder()
                .id(1)
                .waiverDate(LocalDate.now())
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.isAttendanceTarget(Mockito.anyInt())).thenReturn(false);

        // then
        Assertions.assertThrows(DoesNotRegisteredAttendanceException.class, () -> waiverManager.waive(request));
    }

    @Test
    @DisplayName("새로운 출석 면제 추가 실패 테스트 - 서버의 현재 날짜보다 이전 날짜로 면제 신청")
    void CREATE_NEW_WAIVER_FAIL_INVALID_WAIVER_DATE_TEST(){
        // given
        WaiverDto request = WaiverDto.builder()
                .id(1)
                .waiverDate(LocalDate.now().minusDays(1))
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.isAttendanceTarget(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(InvalidWaiverDateException.class, () -> waiverManager.waive(request));
    }

    @Test
    @DisplayName("새로운 출석 면제 추가 실패 테스트 - 서버의 최대 출석 저장 날짜보다 멀리 신청함")
    void CREATE_NEW_WAIVER_FAIL_TOO_FAR_WAIVER_DATE_TEST(){
        // given
        WaiverDto request = WaiverDto.builder()
                .waiverId(1)
                .id(1)
                .waiverDate(LocalDate.now().plusDays(AttendanceRule.ATTENDANCE_SAVED_DATE+1))
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.isAttendanceTarget(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(TooFarDateException.class, () -> waiverManager.waive(request));
    }

    @Test
    @DisplayName("출석 면제 요청 리스트 조회 성공 테스트")
    void READ_ATTENDANCE_WAIVER_LIST_SUCCESS_TEST(){
        WaiverDto waiverDto = WaiverDto.builder()
                .waiverId(1)
                .id(2)
                .userId("hello world")
                .userName("hello world!!!")
                .waiverDate(LocalDate.now())
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.readWaiverList()).thenReturn(List.of(waiverDto));

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(waiverDto.getWaiverId(), waiverDto.getWaiverId()),
                () -> Assertions.assertEquals(waiverDto.getId(), waiverDto.getId()),
                () -> Assertions.assertEquals(waiverDto.getUserId(), waiverDto.getUserId()),
                () -> Assertions.assertEquals(waiverDto.getUserName(), waiverDto.getUserName()),
                () -> Assertions.assertEquals(waiverDto.getWaiverDate(), waiverDto.getWaiverDate()),
                () -> Assertions.assertEquals(waiverDto.getWaiverReason(), waiverDto.getWaiverReason())
        );
    }

    @Test
    @DisplayName("출석 면제 요청 승인 성공 테스트")
    void ACCEPT_ATTENDANCE_WAIVER_SUCCESS_TEST(){
        // given
        WaiverDto waiverDto = WaiverDto.builder()
                .waiverId(1)
                .id(2)
                .userId("hello world")
                .userName("hello world!!!")
                .waiverDate(LocalDate.now())
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.readWaiverByWaiverId(waiverDto.getWaiverId())).thenReturn(Optional.of(waiverDto));

        // then
        Assertions.assertDoesNotThrow(() -> waiverManager.acceptWaiver(waiverDto.getWaiverId(), AttendanceType.ATTENDANCED));
    }

    @Test
    @DisplayName("출석 면제 요청 승인 실패 테스트 - waiver id를 찾을 수 없음")
    void ACCEPT_ATTENDANCE_WAIVER_FAIL_CANNOT_FIND_WAIVER_BY_WAIVER_ID_TEST(){
        // given
        int waiverId = 1;

        // when
        Mockito.when(waiverRepository.readWaiverByWaiverId(waiverId)).thenReturn(Optional.empty());

        // then
        Assertions.assertThrows(UnknownWaiverIdException.class, () -> waiverManager.acceptWaiver(1, AttendanceType.ACKNOWLEDGE_ABSENCE));
    }

    @Test
    @DisplayName("출석 면제 요청 승인 실패 테스트 - 알수없는 AttendanceType")
    void ACCEPT_ATTENDANCE_WAIVER_FAIL_UNKNOWN_ATTENDANCE_TYPE_TEST(){
        // given
        WaiverDto waiverDto = WaiverDto.builder()
                .waiverId(1)
                .id(2)
                .userId("hello world")
                .userName("hello world!!!")
                .waiverDate(LocalDate.now())
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.readWaiverByWaiverId(waiverDto.getWaiverId())).thenReturn(Optional.of(waiverDto));

        // then
        Assertions.assertThrows(UnknownAttendanceTypeException.class, () -> waiverManager.acceptWaiver(1, null));
    }

}