package org.waldreg.repository.attendance.schedule;

import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.attendance.schedule.dto.AttendanceUserStatusDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.attendance.JpaAttendanceTestInitializer;
import org.waldreg.repository.attendance.helper.TestJpaCharacterRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;
import org.waldreg.repository.attendance.repository.JpaAttendanceUserRepository;
import org.waldreg.repository.attendance.repository.JpaUserRepository;
import org.waldreg.repository.attendance.schedule.mapper.ScheduleMapper;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaAttendanceTestInitializer.class, AttendanceScheduleRepositoryServiceProvider.class, ScheduleMapper.class})
@TestPropertySource("classpath:h2-application.properties")
class AttendanceScheduleRepositoryServiceProviderTest{

    @Autowired
    private AttendanceScheduleRepositoryServiceProvider serviceProvider;

    @Autowired
    private JpaAttendanceRepository jpaAttendanceRepository;

    @Autowired
    private JpaAttendanceUserRepository jpaAttendanceUserRepository;

    @Autowired
    private JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    @Qualifier("attendanceJpaUserRepository")
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void INIT_ATTENDANCE_TYPE_REWARD(){
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.LATE_ATTENDANCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ABSENCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ACKNOWLEDGE_ABSENCE.toString()).build());
        jpaAttendanceTypeRewardRepository.save(AttendanceTypeReward.builder().name(AttendanceType.ATTENDANCED.toString()).build());
    }

    @Test
    @DisplayName("모든 출석 대상 유저의 현재 출석 상태 조회 테스트")
    void READ_CURRENT_ATTENDANCE_USERS_ATTENDANCE_STATUS_TEST(){
        // given
        User user1 = getUser("hello");
        AttendanceUser attendanceUser1 = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user1)
                .build());

        User user2 = getUser("bye");
        AttendanceUser attendanceUser2 = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user2)
                .build());

        Attendance attendance1 = jpaAttendanceRepository.save(
                Attendance.builder()
                        .user(attendanceUser1)
                        .attendanceType(jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get())
                        .attendanceDate(LocalDate.now()).build()
        );

        Attendance attendance2 = jpaAttendanceRepository.save(
                Attendance.builder()
                        .user(attendanceUser2)
                        .attendanceType(jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get())
                        .attendanceDate(LocalDate.now()).build()
        );

        entityManager.flush();
        entityManager.clear();

        // when
        List<AttendanceUserStatusDto> resultList = serviceProvider.readAttendanceUserStatusList();

        // then
        assertEquals(2, resultList.size());
        resultList.stream().filter(r -> r.getId() == attendance1.getAttendanceUser().getUser().getId()).findAny().ifPresentOrElse(
                r -> assertAttendance(attendance1, r),
                () -> {throw new IllegalStateException();}
        );
        resultList.stream().filter(r -> r.getId() == attendance2.getAttendanceUser().getUser().getId()).findAny().ifPresentOrElse(
                r -> assertAttendance(attendance2, r),
                () -> {throw new IllegalStateException();}
        );
    }

    @Test
    @DisplayName("모든 출석 대상 유저 STAGING 테스트")
    void STAGE_ALL_ATTENDANCE_USER_TEST(){
        // given
        User user1 = getUser("hello");
        AttendanceUser attendanceUser1 = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user1)
                .build());

        User user2 = getUser("bye");
        AttendanceUser attendanceUser2 = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user2)
                .build());

        entityManager.flush();
        entityManager.clear();

        // when
        serviceProvider.createNewAttendanceCalendar(LocalDate.now());

        List<AttendanceUserStatusDto> result = serviceProvider.readAttendanceUserStatusList();

        // then
        assertEquals(2, result.size());
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

    private void assertAttendance(Attendance expected, AttendanceUserStatusDto result){
        assertAll(
                () -> assertEquals(expected.getAttendanceUser().getUser().getId(), result.getId()),
                () -> assertEquals(expected.getAttendanceType().getName(), result.getAttendanceType().toString())
        );
    }

}
