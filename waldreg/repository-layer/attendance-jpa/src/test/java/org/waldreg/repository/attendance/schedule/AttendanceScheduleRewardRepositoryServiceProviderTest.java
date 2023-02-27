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
import org.springframework.transaction.annotation.Transactional;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaAttendanceTestInitializer.class, AttendanceScheduleRewardRepositoryServiceProvider.class})
@TestPropertySource("classpath:h2-application.properties")
class AttendanceScheduleRewardRepositoryServiceProviderTest{

    @Autowired
    private AttendanceScheduleRewardRepositoryServiceProvider serviceProvider;

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
    @DisplayName("특정 유저에게 상, 벌점 부여 테스트")
    void ASSIGN_REWARD_TO_SPECIFIC_USER_TEST(){
        // given
        User user1 = getUser("hello");
        AttendanceUser attendanceUser1 = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user1)
                .build());

        Attendance attendance1 = jpaAttendanceRepository.save(
                Attendance.builder()
                        .user(attendanceUser1)
                        .attendanceType(jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get())
                        .attendanceDate(LocalDate.now()).build()
        );

        entityManager.flush();
        entityManager.clear();

        // when
        serviceProvider.assignRewardToUser(user1.getId(), AttendanceType.ATTENDANCED);

        Attendance result = jpaAttendanceRepository.findSpecificBetweenAttendanceDate(user1.getId(), LocalDate.now(), LocalDate.now()).get(0);

        // then
        assertAll(
                () -> assertEquals(user1.getId(), result.getAttendanceUser().getUser().getId()),
                () -> assertEquals(LocalDate.now(), result.getAttendanceDate()),
                () -> assertEquals(AttendanceType.ATTENDANCED.toString(), result.getAttendanceType().getName())
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
