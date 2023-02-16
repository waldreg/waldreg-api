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
import org.waldreg.attendance.waiver.dto.WaiverDto;
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
