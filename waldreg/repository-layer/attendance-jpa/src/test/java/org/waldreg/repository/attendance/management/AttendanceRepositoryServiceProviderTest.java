package org.waldreg.repository.attendance.management;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.attendance.management.dto.AttendanceDayDto;
import org.waldreg.attendance.management.dto.AttendanceStatusChangeDto;
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.management.dto.AttendanceUserDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.attendance.JpaAttendanceTestInitializer;
import org.waldreg.repository.attendance.helper.TestJpaCharacterRepository;
import org.waldreg.repository.attendance.management.mapper.AttendanceManagementMapper;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.attendance.repository.JpaUserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaAttendanceTestInitializer.class, AttendanceRepositoryServiceProvider.class, AttendanceManagementMapper.class})
@TestPropertySource("classpath:h2-application.properties")
class AttendanceRepositoryServiceProviderTest{

    @Autowired
    private AttendanceRepositoryServiceProvider serviceProvider;

    @Autowired
    @Qualifier("attendanceJpaUserRepository")
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    private JpaAttendanceUserRepository jpaAttendanceUserRepository;

    @BeforeEach
    void INIT_ATTENDANCE_TYPE_REWARD(){
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.LATE_ATTENDANCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ABSENCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ACKNOWLEDGE_ABSENCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ATTENDANCED.toString()).build());
    }

    @Test
    @DisplayName("유저 출석 대상 등록 테스트")
    void REGISTER_USER_TO_ATTENDANCE_USER_TEST(){
        // given
        User user = getUser("hello");

        // when
        serviceProvider.registerAttendanceTarget(user.getId());

        boolean result = jpaAttendanceUserRepository.existsByUserId(user.getId());

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("유저를 출석 대상에서 제거 테스트")
    void DELETE_REGISTERED_USER_TO_ATTENDANCE_USER_TEST(){
        // given
        User user = getUser("hello");

        // when
        serviceProvider.registerAttendanceTarget(user.getId());

        boolean isSaved = jpaAttendanceUserRepository.existsByUserId(user.getId());

        serviceProvider.deleteRegisteredAttendanceTarget(user.getId());

        boolean result = jpaAttendanceUserRepository.existsByUserId(user.getId());

        // then
        assertTrue(isSaved);
        assertFalse(result);
    }

    @Test
    @DisplayName("출석 대상 유저의 출석 상태 조회 테스트")
    void READ_ATTENDANCE_TARGET_DTO_TEST(){
        // given
        User user = getUser("hello");
        serviceProvider.registerAttendanceTarget(user.getId());
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now());

        // when
        AttendanceTargetDto result = serviceProvider.readAttendanceTarget(user.getId()).get();

        // then
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getUserId(), result.getUserId()),
                () -> assertEquals(AttendanceType.ABSENCE, result.getAttendanceStatus())
        );
    }

    @Test
    @DisplayName("출석 대상 유저의 출석 상태 강제 변경 테스트")
    void FORCE_CHANGE_ATTENDANCE_USER_ATTENDANCE_STATUS_TEST(){
        // given
        User user = getUser("hello");
        serviceProvider.registerAttendanceTarget(user.getId());
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now());

        // when
        serviceProvider.changeAttendanceStatus(AttendanceStatusChangeDto.builder()
                .id(user.getId())
                .attendanceDate(LocalDate.now())
                .attendanceType(AttendanceType.ACKNOWLEDGE_ABSENCE)
                .build());

        AttendanceTargetDto result = serviceProvider.readAttendanceTarget(user.getId()).get();

        // then
        assertAll(
                () -> assertEquals(user.getId(), result.getId()),
                () -> assertEquals(user.getUserId(), result.getUserId()),
                () -> assertEquals(AttendanceType.ACKNOWLEDGE_ABSENCE, result.getAttendanceStatus())
        );
    }

    @Test
    @DisplayName("모든 유저의 출석 상태 조회 테스트")
    void READ_ALL_USERS_ATTENDANCE_STATUS_LIST(){
        // given
        User user1 = getUser("hello1");
        serviceProvider.registerAttendanceTarget(user1.getId());

        User user2 = getUser("hello2");
        serviceProvider.registerAttendanceTarget(user2.getId());

        User user3 = getUser("hello3");
        serviceProvider.registerAttendanceTarget(user3.getId());

        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now());
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now().plusDays(1));
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now().plusDays(2));

        // when
        List<AttendanceDayDto> attendanceDayDtoList = serviceProvider.readAttendanceStatusList(LocalDate.now(), LocalDate.now().plusDays(2));

        // then
        assertAll(
                () -> assertEquals(3, attendanceDayDtoList.size()),
                () -> assertEquals(3, attendanceDayDtoList.get(0).getAttendanceUserList().size()),
                () -> assertEquals(3, attendanceDayDtoList.get(1).getAttendanceUserList().size()),
                () -> assertEquals(3, attendanceDayDtoList.get(2).getAttendanceUserList().size())
        );
    }

    @Test
    @DisplayName("단일 유저의 출석 상태 조회 테스트")
    void READ_SPECIFIC_USER_ATTENDANCE_STATUS_TEST(){
        // given
        User user1 = getUser("hello1");
        serviceProvider.registerAttendanceTarget(user1.getId());

        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now());
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now().plusDays(1));
        serviceProvider.createNewAttendanceCalendarIfAbsent(LocalDate.now().plusDays(2));

        // when
        AttendanceUserDto attendanceUserDto = serviceProvider
                .readSpecificAttendanceStatusList(user1.getId(), LocalDate.now(), LocalDate.now().plusDays(2));

        // then
        assertAll(
                () -> assertEquals(3, attendanceUserDto.getAttendanceUserStatusList().size()),
                () -> assertEquals(user1.getId(), attendanceUserDto.getId()),
                () -> assertEquals(user1.getUserId(), attendanceUserDto.getUserId()),
                () -> assertEquals(user1.getName(), attendanceUserDto.getUserName())
        );
    }

    private User getUser(String userId){
        return jpaUserRepository.save(User.builder()
                .userId(userId)
                .userPassword(userId)
                .name(userId)
                .character(getCharacter(userId))
                .phoneNumber("010-0000-0000")
                .build());
    }

    private Character getCharacter(String name){
        return jpaCharacterRepository.saveAndFlush(Character.builder()
                .characterName(name)
                .permissionList(List.of())
                .build());
    }

}
