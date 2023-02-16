package org.waldreg.repository.attendance.management;

import java.time.LocalDate;
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
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.attendance.management.mapper.AttendanceManagementMapper;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AttendanceManagementRepository.class,
        MemoryAttendanceStorage.class,
        MemoryUserStorage.class,
        AttendanceManagementMapper.class,
        MemoryCharacterStorage.class})
class AttendanceManagementRepositoryTest{

    @Autowired
    private AttendanceManagementRepository repository;

    @Autowired
    private MemoryAttendanceStorage attendanceStorage;

    @Autowired
    private MemoryUserStorage userStorage;

    @BeforeEach
    @AfterEach
    void init(){
        userStorage.deleteAllUser();
        attendanceStorage.deleteAll();
    }

    @Test
    @DisplayName("유저 출석대상 등록 및 조회 성공 테스트")
    void REGISTER_USER_ON_ATTENDANCE_TARGET_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();

        // when
        repository.registerAttendanceTarget(user.getId());
        AttendanceTargetDto result = repository.readAttendanceTarget(user.getId()).orElseThrow(IllegalStateException::new);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(user.getId(), result.getId()),
                () -> Assertions.assertEquals(AttendanceType.ABSENCE, result.getAttendanceStatus())
        );
    }

    @Test
    @DisplayName("유저 출석대상 등록 및 삭제 성공 테스트")
    void REGISTER_USER_ON_ATTENDANCE_TARGET_AND_DELETE_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();

        // when
        repository.registerAttendanceTarget(user.getId());
        repository.deleteRegisteredAttendanceTarget(user.getId());

        // then
        Assertions.assertThrows(IllegalStateException.class,
                () -> repository.readAttendanceTarget(user.getId()).orElseThrow(IllegalStateException::new));
    }

    @Test
    @DisplayName("유저 출석대상 등록 및 수정 성공 테스트")
    void REGISTER_USER_AND_CHANGE_SUCCESS_TEST(){
        // given
        LocalDate targetDate = LocalDate.now().minusDays(1);
        User user = addAndGetDefaultUser();
        AttendanceStatusChangeDto attendanceStatusChangeDto = AttendanceStatusChangeDto.builder()
                .id(user.getId())
                .attendanceDate(targetDate)
                .attendanceType(AttendanceType.ACKNOWLEDGE_ABSENCE)
                .build();

        // when
        repository.registerAttendanceTarget(user.getId());
        repository.changeAttendanceStatus(attendanceStatusChangeDto);
        AttendanceUserDto result = repository.readSpecificAttendanceStatusList(user.getId(), targetDate, targetDate);

        // then
        result.getAttendanceUserStatusList().stream()
                .filter(s -> s.getAttendanceDate().isEqual(targetDate))
                .findAny()
                .ifPresent(
                        s -> Assertions.assertEquals(AttendanceType.ACKNOWLEDGE_ABSENCE, s.getAttendanceStatus())
                );
    }

    @Test
    @DisplayName("모든 출석 대상 조회 성공 테스트")
    void READ_ALL_ATTENDANCE_TARGET_SUCCESS_TEST(){
        // given
        User user1 = addAndGetDefaultUser();

        // when
        repository.registerAttendanceTarget(user1.getId());
        List<AttendanceDayDto> result = repository.readAttendanceStatusList(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));

        // then
        result.forEach(
                r -> {
                    System.out.println(r.getAttendanceDate());
                    r.getAttendanceUserList().forEach(System.out::println);
                    if(r.getAttendanceDate().isEqual(LocalDate.now())){
                        Assertions.assertEquals(1, r.getAttendanceUserList().size());
                        Assertions.assertEquals("Default", r.getAttendanceUserList().get(0).getUserId());
                        Assertions.assertEquals("Default", r.getAttendanceUserList().get(0).getUserName());
                        return;
                    }
                    Assertions.assertEquals(1, r.getAttendanceUserList().size());
                }
        );
    }

    @Test
    @DisplayName("특정 유저 출석 조회 성공 테스트")
    void READ_SPECIFIC_USERS_ATTENDANCE_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();

        // when
        repository.registerAttendanceTarget(user.getId());
        AttendanceUserDto attendanceUserDto = repository.readSpecificAttendanceStatusList(user.getId(), LocalDate.now(), LocalDate.now());

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), attendanceUserDto.getId()),
                () -> Assertions.assertEquals(user.getUserId(), attendanceUserDto.getUserId()),
                () -> Assertions.assertEquals(user.getName(), attendanceUserDto.getUserName()),
                () -> Assertions.assertEquals(AttendanceType.ABSENCE, attendanceUserDto.getAttendanceUserStatusList().get(0).getAttendanceStatus())
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
