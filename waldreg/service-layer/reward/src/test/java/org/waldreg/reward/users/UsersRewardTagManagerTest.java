package org.waldreg.reward.users;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wadlreg.reward.users.DefaultUsersRewardTagManager;
import org.wadlreg.reward.users.UsersRewardTagManager;
import org.wadlreg.reward.users.dto.UsersRewardDto;
import org.wadlreg.reward.users.dto.UsersRewardTagDto;
import org.wadlreg.reward.users.spi.UsersRewardTagManagerRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DefaultUsersRewardTagManager.class)
public class UsersRewardTagManagerTest{

    @Autowired
    private UsersRewardTagManager usersRewardTagManager;

    @MockBean
    private UsersRewardTagManagerRepository usersRewardTagManagerRepository;

    @Test
    @DisplayName("특정 유저에게 상점 부여 성공 테스트")
    public void ASSIGN_REWARD_TO_USER_TEST(){
        // given
        int id = 1;
        int rewardTagId = 1;

        // when & then
        Assertions.assertDoesNotThrow(() -> usersRewardTagManager.assignRewardTagToUser(id, rewardTagId));
    }

    @Test
    @DisplayName("특정 유저의 상점 태그목록 조회 성공 테스트")
    public void READ_SPECIFY_USERS_REWARD_TAG_SUCCESS_TEST(){
        // given
        int id = 1;
        UsersRewardDto usersRewardDto = UsersRewardDto.builder()
                .id(id)
                .name("hello world")
                .userId("hello123")
                .reward(1)
                .usersRewardTagDtoList(List.of(
                        UsersRewardTagDto.builder()
                                .rewardId(1)
                                .rewardTagId(1)
                                .rewardTagTitle("contest winner")
                                .rewardPoint(100)
                                .build()
                ))
                .build();

        // when
        Mockito.when(usersRewardTagManagerRepository.readSpecifyUsersReward(1)).thenReturn(usersRewardDto);
        UsersRewardDto result = usersRewardTagManager.readSpecifyUsersReward(1);

        // then
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("특정 유저에게 부여된 상 벌점 삭제 성공 테스트")
    public void DELETE_REWARD_TAG_ASSIGNED_TO_SPECIFY_USERS_SUCCESS_TEST(){
        // given
        int id = 1;
        int rewardId = 1;

        // when & then
        Assertions.assertDoesNotThrow(() -> usersRewardTagManager.deleteRewardToUser(id, rewardId));
    }

}