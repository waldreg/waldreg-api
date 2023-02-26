package org.waldreg.repository.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;
import org.waldreg.repository.attendance.helper.TestJpaCharacterRepository;
import org.waldreg.repository.attendance.helper.TestJpaUserRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaAttendanceRepositoryTest{

    @Autowired
    private JpaAttendanceUserRepository jpaAttendanceUserRepository;

    @Autowired
    private TestJpaUserRepository jpaUserRepository;

    @Autowired
    private JpaAttendanceRepository jpaAttendanceRepository;

    @Autowired
    private JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private JpaRewardTagRepository jpaRewardTagRepository;

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
    @DisplayName("User를 출석 대상에 등록 및 조회 테스트")
    void REGISTER_ATTENDANCE_USER_TEST(){
        // given
        User user = getUser("hello");
        AttendanceUser attendanceUser = AttendanceUser.builder()
                .user(user)
                .build();

        // when
        jpaUserRepository.save(user);
        AttendanceUser expected = jpaAttendanceUserRepository.saveAndFlush(attendanceUser);

        entityManager.clear();

        AttendanceUser result = jpaAttendanceUserRepository.findById(expected.getAttendanceUserId()).get();

        // then
        assertAttendanceUser(expected, result);
    }

    @Test
    @DisplayName("User를 출석 대상에서 삭제 테스트")
    void DELETE_ATTENDANCE_USER_TEST(){
        // given
        User user = getUser("hello");
        AttendanceUser attendanceUser = AttendanceUser.builder()
                .user(user)
                .build();

        // when
        User saved = jpaUserRepository.save(user);
        AttendanceUser expected = jpaAttendanceUserRepository.saveAndFlush(attendanceUser);

        jpaAttendanceUserRepository.deleteByUsersId(saved.getId());

        entityManager.clear();

        Optional<AttendanceUser> result = jpaAttendanceUserRepository.findById(expected.getAttendanceUserId());

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("출석대상의 출석 상태 변경 테스트")
    void CHANGE_ATTENDANCE_STATUS_TEST(){
        // given
        User user = getUser("hello");
        AttendanceUser attendanceUser = AttendanceUser.builder()
                .user(user)
                .build();

        AttendanceTypeReward beforeState = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get();
        AttendanceTypeReward changedState = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.LATE_ATTENDANCE.toString()).get();

        // when
        jpaUserRepository.save(user);
        AttendanceUser attendant = jpaAttendanceUserRepository.saveAndFlush(attendanceUser);

        Attendance attendance = Attendance.builder()
                .user(attendant)
                .attendanceType(beforeState)
                .attendanceDate(LocalDate.now())
                .build();

        Attendance saved = jpaAttendanceRepository.saveAndFlush(attendance);

        saved.setAttendanceType(changedState);

        entityManager.flush();
        entityManager.clear();

        Attendance result = jpaAttendanceRepository.findById(saved.getAttendanceId()).get();

        // then
        assertAttendanceUser(saved.getAttendanceUser(), result.getAttendanceUser());
        assertEquals(AttendanceType.LATE_ATTENDANCE.toString(), result.getAttendanceType().getName());
    }

    @Test
    @DisplayName("출석 현황 조회 테스트")
    void FIND_ATTENDANCE_BETWEEN_TEST(){
        // given
        User user = getUser("hello");
        user = jpaUserRepository.save(user);
        AttendanceUser attendanceUser = AttendanceUser.builder()
                .user(user)
                .build();
        attendanceUser = jpaAttendanceUserRepository.save(attendanceUser);

        AttendanceTypeReward state = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get();

        Attendance attendance1 = Attendance.builder()
                .user(attendanceUser)
                .attendanceType(state)
                .attendanceDate(LocalDate.now())
                .build();

        Attendance attendance2 = Attendance.builder()
                .user(attendanceUser)
                .attendanceType(state)
                .attendanceDate(LocalDate.now().minusYears(1))
                .build();

        Attendance attendance3 = Attendance.builder()
                .user(attendanceUser)
                .attendanceType(state)
                .attendanceDate(LocalDate.now().minusYears(2))
                .build();

        // when
        jpaAttendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3));

        entityManager.flush();
        entityManager.clear();

        List<Attendance> result = jpaAttendanceRepository
                .findBetweenAttendanceDate(LocalDate.now().minusYears(2).plusDays(1), LocalDate.now());

        // then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("특정 유저의 출석 현황 조회 테스트")
    void FIND_SPECIFIC_ATTENDANCE_BETWEEN_TEST(){
        // given
        User user1 = getUser("hello");
        User user2 = getUser("bye");
        user1 = jpaUserRepository.save(user1);
        user2 = jpaUserRepository.save(user2);
        AttendanceUser attendanceUser1 = AttendanceUser.builder()
                .user(user1)
                .build();
        AttendanceUser attendanceUser2 = AttendanceUser.builder()
                .user(user2)
                .build();
        attendanceUser1 = jpaAttendanceUserRepository.save(attendanceUser1);
        attendanceUser2 = jpaAttendanceUserRepository.save(attendanceUser2);

        AttendanceTypeReward state = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get();

        Attendance attendance1 = Attendance.builder()
                .user(attendanceUser1)
                .attendanceType(state)
                .attendanceDate(LocalDate.now())
                .build();

        Attendance attendance2 = Attendance.builder()
                .user(attendanceUser2)
                .attendanceType(state)
                .attendanceDate(LocalDate.now().minusYears(1))
                .build();

        Attendance attendance3 = Attendance.builder()
                .user(attendanceUser1)
                .attendanceType(state)
                .attendanceDate(LocalDate.now().minusYears(2))
                .build();

        // when
        jpaAttendanceRepository.saveAll(List.of(attendance1, attendance2, attendance3));

        entityManager.flush();
        entityManager.clear();

        List<Attendance> result = jpaAttendanceRepository
                .findSpecificBetweenAttendanceDate(user1.getId(), LocalDate.now().minusYears(5), LocalDate.now());

        // then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("출석 상태에 상점 태그 부여 테스트")
    void SET_REWARD_TAG_ON_ATTENDANCE_STATUS_TEST(){
        // given
        AttendanceTypeReward attendanceTypeReward = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get();

        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("Hello world")
                .rewardPoint(1000)
                .build();

        // when
        RewardTag saved = jpaRewardTagRepository.save(rewardTag);

        attendanceTypeReward.setRewardTag(saved);

        entityManager.flush();
        entityManager.clear();

        AttendanceTypeReward result = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ABSENCE.toString()).get();

        // then
        assertAll(
                () -> assertEquals(saved.getRewardTagId(), result.getRewardTag().getRewardTagId()),
                () -> assertEquals(saved.getRewardTagTitle(), result.getRewardTag().getRewardTagTitle()),
                () -> assertEquals(saved.getRewardPoint(), result.getRewardTag().getRewardPoint())
        );
    }

    private User getUser(String userId){
        return User.builder()
                .userId(userId)
                .userPassword(userId)
                .name(userId)
                .character(getCharacter(userId))
                .phoneNumber("010-0000-0000")
                .build();
    }

    private Character getCharacter(String name){
        return jpaCharacterRepository.saveAndFlush(Character.builder()
                .characterName(name)
                .permissionList(List.of())
                .build());
    }

    private void assertAttendanceUser(AttendanceUser expected, AttendanceUser result){
        assertAll(
                () -> assertEquals(expected.getAttendanceUserId(), result.getAttendanceUserId()),
                () -> assertEquals(expected.getUser().getId(), result.getUser().getId()),
                () -> assertEquals(expected.getUser().getUserId(), result.getUser().getUserId()),
                () -> assertEquals(expected.getUser().getUserPassword(), result.getUser().getUserPassword()),
                () -> assertEquals(expected.getUser().getName(), result.getUser().getName()),
                () -> assertEquals(expected.getUser().getCreatedAt(), result.getUser().getCreatedAt())
        );
    }

}
