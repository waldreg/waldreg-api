package org.waldreg.repository.attendance.waiver;

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
import org.waldreg.attendance.management.dto.AttendanceTargetDto;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.attendance.waiver.dto.WaiverDto;
import org.waldreg.domain.attendance.Attendance;
import org.waldreg.domain.attendance.AttendanceUser;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.MemoryWaiverStorage;
import org.waldreg.repository.attendance.waiver.mapper.MemoryWaiverMapper;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryWaiverRepository.class,
        MemoryWaiverStorage.class,
        MemoryAttendanceStorage.class,
        MemoryUserStorage.class,
        MemoryCharacterStorage.class,
        MemoryWaiverMapper.class})
class MemoryWaiverRepositoryTest{

    @Autowired
    private MemoryWaiverRepository memoryWaiverRepository;

    @Autowired
    private MemoryWaiverStorage memoryWaiverStorage;

    @Autowired
    private MemoryAttendanceStorage memoryAttendanceStorage;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @BeforeEach
    @AfterEach
    void INIT(){
        memoryAttendanceStorage.deleteAll();
        memoryUserStorage.deleteAllUser();
        memoryWaiverStorage.deleteAll();
    }

    @Test
    @DisplayName("출석 면제 신청 및 조회 성공 테스트")
    void WAIVE_AND_READ_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();
        WaiverDto waiverDto = WaiverDto.builder()
                .id(user.getId())
                .waiverDate(LocalDate.now())
                .waiverReason("test waiver")
                .build();

        // when
        memoryWaiverRepository.waive(waiverDto);
        WaiverDto result = memoryWaiverRepository.readWaiverList().get(0);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(user.getName(), result.getUserName()),
                () -> Assertions.assertEquals(user.getId(), result.getId()),
                () -> Assertions.assertEquals(waiverDto.getWaiverDate(), result.getWaiverDate()),
                () -> Assertions.assertEquals(waiverDto.getWaiverReason(), result.getWaiverReason())
        );
    }

    @Test
    @DisplayName("출석 면제 신청 승인 성공 테스트")
    void ACCEPT_WAIVER_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();
        WaiverDto waiverDto = WaiverDto.builder()
                .id(user.getId())
                .waiverDate(LocalDate.now().minusDays(7))
                .waiverReason("test waiver")
                .build();

        // when
        memoryAttendanceStorage.addAttendanceTarget(AttendanceUser.builder().user(user).build());
        memoryWaiverRepository.waive(waiverDto);
        WaiverDto waiver = memoryWaiverRepository.readWaiverList().get(0);
        memoryWaiverRepository.acceptWaiver(waiver.getWaiverId(), AttendanceType.ACKNOWLEDGE_ABSENCE);
        List<Attendance> result = memoryAttendanceStorage.readSpecificUsersAttendance(user.getId(), LocalDate.now().minusDays(7), LocalDate.now().minusDays(7));

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(AttendanceType.ACKNOWLEDGE_ABSENCE.toString(), result.get(0).getAttendanceType().getName()),
                () -> Assertions.assertEquals(user.getId(), result.get(0).getAttendanceUser().getUser().getId())
        );
    }

    @Test
    @DisplayName("출석 면제 신청 삭제 및 조회 성공 테스트")
    void WAIVER_DELETE_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();
        WaiverDto waiverDto = WaiverDto.builder()
                .id(user.getId())
                .waiverDate(LocalDate.now().minusDays(7))
                .waiverReason("test waiver")
                .build();

        // when
        memoryWaiverRepository.waive(waiverDto);
        WaiverDto waiver = memoryWaiverRepository.readWaiverList().get(0);
        memoryWaiverRepository.deleteWaiver(waiver.getWaiverId());
        WaiverDto result = memoryWaiverRepository.readWaiverByWaiverId(waiver.getWaiverId()).orElse(null);

        // then
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("단일 공결 신청 조회 성공 테스트")
    void READ_WAIVER_BY_WAIVER_ID_SUCCESS_TEST(){
        // given
        User user = addAndGetDefaultUser();
        WaiverDto waiverDto = WaiverDto.builder()
                .id(user.getId())
                .waiverDate(LocalDate.now().minusDays(7))
                .waiverReason("test waiver")
                .build();

        // when
        memoryWaiverRepository.waive(waiverDto);
        WaiverDto waiver = memoryWaiverRepository.readWaiverList().get(0);
        WaiverDto result = memoryWaiverRepository.readWaiverByWaiverId(waiver.getWaiverId()).orElseThrow(IllegalAccessError::new);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(waiverDto.getId(), result.getId()),
                () -> Assertions.assertEquals(waiverDto.getWaiverDate(), result.getWaiverDate()),
                () -> Assertions.assertEquals(waiverDto.getWaiverReason(), result.getWaiverReason())
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
        memoryUserStorage.createUser(user);
        return memoryUserStorage.readUserByUserId("Default");
    }

}
