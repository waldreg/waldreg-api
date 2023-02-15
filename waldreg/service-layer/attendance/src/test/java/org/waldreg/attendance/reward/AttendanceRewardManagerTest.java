package org.waldreg.attendance.reward;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownRewardTagIdException;
import org.waldreg.attendance.reward.spi.AttendanceRewardRepository;
import org.waldreg.attendance.type.AttendanceType;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultAttendanceRewardManager.class})
class AttendanceRewardManagerTest{

    @Autowired
    private AttendanceRewardManager attendanceRewardManager;

    @MockBean
    private AttendanceRewardRepository attendanceRewardRepository;

    @Test
    @DisplayName("출석 태그에 상점 부여 성공 테스트")
    void SET_REWARD_TAG_TO_ATTENDANCE_TYPE_SUCCESS_TEST(){
        // when
        Mockito.when(attendanceRewardRepository.isRewardTagExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> attendanceRewardManager.setRewardTag(1, AttendanceType.LATE_ATTENDANCE));
    }

    @Test
    @DisplayName("출석 태그에 상점 부여 실패 테스트 - reward-tag 가 없음")
    void SET_REWARD_TAG_TO_ATTENDANCE_TYPE_FAIL_UNKNOWN_REWARD_TAG_TEST(){
        // when
        Mockito.when(attendanceRewardRepository.isRewardTagExist(Mockito.anyInt())).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownRewardTagIdException.class, () -> attendanceRewardManager.setRewardTag(1, AttendanceType.LATE_ATTENDANCE));
    }

    @Test
    @DisplayName("출석 태그에 상점 부여 실패 테스트 - 알수없는 AttendanceType")
    void SET_REWARD_TAG_TO_ATTENDANCE_TYPE_FAIL_UNKNOWN_ATTENDANCE_TYPE_TEST(){
        // when
        Mockito.when(attendanceRewardRepository.isRewardTagExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(UnknownAttendanceTypeException.class, () -> attendanceRewardManager.setRewardTag(1, null));
    }

}
