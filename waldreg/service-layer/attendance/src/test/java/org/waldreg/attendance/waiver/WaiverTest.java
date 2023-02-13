package org.waldreg.attendance.waiver;

import java.time.LocalDate;
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
import org.waldreg.attendance.rule.AttendanceRule;
import org.waldreg.attendance.rule.valid.AttendanceDateValidator;
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
                .id(1)
                .waiverDate(LocalDate.now().plusDays(AttendanceRule.ATTENDANCE_SAVED_DATE+1))
                .waiverReason("for test")
                .build();

        // when
        Mockito.when(waiverRepository.isAttendanceTarget(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(TooFarDateException.class, () -> waiverManager.waive(request));
    }

}