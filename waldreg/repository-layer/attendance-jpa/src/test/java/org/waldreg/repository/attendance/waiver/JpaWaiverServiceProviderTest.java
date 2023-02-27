package org.waldreg.repository.attendance.waiver;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
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
import org.waldreg.repository.attendance.waiver.mapper.WaiverMapper;

@DataJpaTest
@ContextConfiguration(classes = {JpaAttendanceTestInitializer.class, WaiverRepositoryServiceProvider.class, WaiverMapper.class})
@TestPropertySource("classpath:h2-application.properties")
class JpaWaiverServiceProviderTest{

    @Autowired
    private WaiverRepositoryServiceProvider serviceProvider;

    @Autowired
    @Qualifier("attendanceJpaUserRepository")
    private JpaUserRepository jpaWaiverUserRepository;

    @Autowired
    private TestJpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private JpaAttendanceUserRepository jpaAttendanceUserRepository;

    @Autowired
    private JpaAttendanceRepository jpaAttendanceRepository;

    @Autowired
    private JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

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
    @DisplayName("출석 면제 등록 및 조회 테스트")
    void WAIVE_TEST(){
        // given
        User user = jpaWaiverUserRepository.save(getUser("hello"));
        WaiverDto request = WaiverDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getName())
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("Sick!")
                .build();

        // when
        serviceProvider.waive(request);

        entityManager.clear();

        WaiverDto result = serviceProvider.readWaiverList().get(0);

        // then
        assertWaiverDto(request, result);
    }

    @Test
    @DisplayName("출석 면제 등록 및 단일 조회 테스트")
    void WAIVE_SPECIFIC_TEST(){
        // given
        User user = jpaWaiverUserRepository.save(getUser("hello"));
        WaiverDto request = WaiverDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getName())
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("Sick!")
                .build();

        // when
        serviceProvider.waive(request);

        entityManager.clear();

        WaiverDto temp = serviceProvider.readWaiverList().get(0);
        WaiverDto result = serviceProvider.readWaiverByWaiverId(temp.getWaiverId()).get();

        // then
        assertWaiverDto(request, result);
    }

    @Test
    @DisplayName("출석 면제 요청 삭제 테스트")
    void DELETE_WAIVER_TEST(){
        // given
        User user = jpaWaiverUserRepository.save(getUser("hello"));
        WaiverDto request = WaiverDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getName())
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("Sick!")
                .build();

        // when
        serviceProvider.waive(request);
        WaiverDto temp = serviceProvider.readWaiverList().get(0);
        serviceProvider.deleteWaiver(temp.getWaiverId());
        Optional<WaiverDto> result = serviceProvider.readWaiverByWaiverId(temp.getWaiverId());

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("출석 대상 확인 테스트")
    void CHECK_ATTENDANCE_TARGET_TEST(){
        // given
        User target = jpaWaiverUserRepository.save(getUser("target"));
        User noneTarget = jpaWaiverUserRepository.save(getUser("none-target"));

        // when
        jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(target)
                .build());

        boolean targetResult = serviceProvider.isAttendanceTarget(target.getId());
        boolean noneTargetResult = serviceProvider.isAttendanceTarget(noneTarget.getId());

        // then
        assertTrue(targetResult);
        assertFalse(noneTargetResult);
    }

    @Test
    @DisplayName("출석 면제 요청 승인 테스트")
    void ACCEPT_WAIVE_TEST(){
        // given
        User user = jpaWaiverUserRepository.save(getUser("user"));
        AttendanceUser attendanceUser = jpaAttendanceUserRepository.save(AttendanceUser.builder()
                .user(user)
                .build());
        WaiverDto request = WaiverDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userName(user.getName())
                .waiverDate(LocalDate.now().plusDays(1))
                .waiverReason("Sick!")
                .build();

        // when
        serviceProvider.waive(request);
        WaiverDto temp = serviceProvider.readWaiverList().get(0);
        serviceProvider.acceptWaiver(temp.getWaiverId(), AttendanceType.ACKNOWLEDGE_ABSENCE);

        Attendance result = jpaAttendanceRepository
                .findSpecificBetweenAttendanceDate(user.getId(),
                        LocalDate.now().plusDays(1),
                        LocalDate.now().plusDays(1))
                .get(0);

        // then
        assertAll(
                () -> assertEquals(LocalDate.now().plusDays(1), result.getAttendanceDate()),
                () -> assertEquals(AttendanceType.ACKNOWLEDGE_ABSENCE.toString(), result.getAttendanceType().getName())
        );
    }

    private void assertWaiverDto(WaiverDto expected, WaiverDto result){
        assertAll(
                () -> assertNotNull(result.getWaiverId()),
                () -> assertEquals(expected.getWaiverDate(), result.getWaiverDate()),
                () -> assertEquals(expected.getWaiverReason(), result.getWaiverReason()),
                () -> assertEquals(expected.getId(), result.getId()),
                () -> assertEquals(expected.getUserId(), result.getUserId())
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

}
