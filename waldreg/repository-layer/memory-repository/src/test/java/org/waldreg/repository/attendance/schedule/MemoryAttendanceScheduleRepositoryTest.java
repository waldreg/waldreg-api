package org.waldreg.repository.attendance.schedule;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.attendance.schedule.mapper.MemoryAttendanceScheduleMapper;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        MemoryAttendanceScheduleRepository.class,
        MemoryAttendanceStorage.class,
        MemoryAttendanceScheduleMapper.class,
        MemoryUserStorage.class,
        MemoryCharacterStorage.class
})
class MemoryAttendanceScheduleRepositoryTest{

    @Autowired
    private MemoryAttendanceScheduleRepository repository;

    @Autowired
    private MemoryAttendanceStorage attendanceStorage;

    @Autowired
    private MemoryUserStorage userStorage;

    @BeforeEach
    @AfterEach
    void INIT(){
        userStorage.deleteAllUser();
        attendanceStorage.deleteAll();
    }

    @Test
    @DisplayName("주기마다 유저 출석 상태 조회 성공 테스트")
    void READ_USERS_ATTENDANCE_STATUS_SCHEDULE_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();

        // when
        attendanceStorage.addAttendanceTarget(AttendanceUser.builder().user(user).build());
        attendanceStorage.stageAttendanceUser();
        List<AttendanceUserStatusDto> result = repository.readAttendanceUserStatusList();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(user.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(AttendanceType.ABSENCE, result.get(0).getAttendanceType())
        );
    }

    private User addAndGetDefaultUser(){
        User user = User.builder()
                .userId("Default")
                .name("Default")
                .userPassword("Default")
                .socialLogin(List.of())
                .phoneNumber("Default")
                .build();
        userStorage.createUser(user);
        return userStorage.readUserByUserId("Default");
    }

}
