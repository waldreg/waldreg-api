package org.waldreg.repository.attendance.reward;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.waldreg.attendance.type.AttendanceType;
import org.waldreg.domain.attendance.AttendanceTypeReward;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.repository.attendance.JpaAttendanceTestInitializer;
import org.waldreg.repository.attendance.repository.JpaAttendanceTypeRewardRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {JpaAttendanceTestInitializer.class, AttendanceRewardRepositoryServiceProvider.class})
class AttendanceRewardRepositoryServiceProviderTest{

    @Autowired
    private AttendanceRewardRepositoryServiceProvider serviceProvider;

    @Autowired
    private JpaAttendanceTypeRewardRepository jpaAttendanceTypeRewardRepository;

    @Autowired
    private TestJpaRewardTagRepository jpaRewardTagRepository;

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
    @DisplayName("RewardTag 존재 확인 테스트")
    void EXIST_REWARD_TAG_BY_ID_TEST(){
        // given
        RewardTag rewardTag = jpaRewardTagRepository.save(RewardTag.builder()
                .rewardTagTitle("Test")
                .rewardPoint(100)
                .build());

        // when
        boolean result = serviceProvider.isRewardTagExist(rewardTag.getRewardTagId());

        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("AttendanceRewardType에 RewardTag세팅 테스트")
    void SET_REWARD_TAG_TO_ATTENDANCE_REWARD_TYPE_TEST(){
        // given
        RewardTag rewardTag = jpaRewardTagRepository.save(RewardTag.builder()
                .rewardTagTitle("ACKNOWLEDGE_ATTENDANCE")
                .rewardPoint(100)
                .build());

        // when
        serviceProvider.setRewardTag(rewardTag.getRewardTagId(), AttendanceType.ACKNOWLEDGE_ABSENCE);

        AttendanceTypeReward result = jpaAttendanceTypeRewardRepository.findByName(AttendanceType.ACKNOWLEDGE_ABSENCE.toString()).get();

        // then
        assertAll(
                () -> assertEquals(result.getRewardTag().getRewardTagId(), rewardTag.getRewardTagId()),
                () -> assertEquals(result.getRewardTag().getRewardPoint(), rewardTag.getRewardPoint()),
                () -> assertEquals(result.getRewardTag().getRewardTagTitle(), rewardTag.getRewardTagTitle())
        );
    }

}
