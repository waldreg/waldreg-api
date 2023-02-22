package org.waldreg.repository.attendance.reward;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.MemoryAttendanceStorage;
import org.waldreg.repository.MemoryRewardTagStorage;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryAttendanceRewardRepository.class,
        MemoryAttendanceStorage.class,
        MemoryRewardTagStorage.class})
class MemoryAttendanceRewardRepositoryTest{

    @Autowired
    private MemoryAttendanceRewardRepository rewardRepository;

    @Autowired
    private MemoryAttendanceStorage attendanceStorage;

    @Autowired
    private MemoryRewardTagStorage rewardTagStorage;

    @BeforeEach
    @AfterEach
    void INIT(){
        attendanceStorage.deleteAll();
        rewardTagStorage.deleteAll();
    }

    @Test
    @DisplayName("Reward tag 출석 상태에 부여 성공 테스트")
    void ASSIGN_REWARD_TAG_TO_ATTENDANCE_STATUS(){
        // given
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("ACKNOWLEDGE_ATTENDANCE")
                .rewardPoint(10)
                .build();

        // when
        rewardTagStorage.createRewardTag(rewardTag);
        RewardTag savedTag = rewardTagStorage.readRewardTagList().get(0);

        // then
        Assertions.assertDoesNotThrow(() -> rewardRepository.setRewardTag(savedTag.getRewardTagId(), AttendanceType.ACKNOWLEDGE_ABSENCE));
    }

}
