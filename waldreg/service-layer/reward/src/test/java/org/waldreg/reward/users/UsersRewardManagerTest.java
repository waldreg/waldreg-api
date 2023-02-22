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
import org.waldreg.reward.exception.UnknownRewardException;
import org.waldreg.reward.exception.UnknownRewardTargetException;
import org.waldreg.reward.exception.UnknownRewardTagException;
import org.waldreg.reward.users.dto.UsersRewardDto;
import org.waldreg.reward.users.dto.UsersRewardTagDto;
import org.waldreg.reward.users.spi.repository.UserExistChecker;
import org.waldreg.reward.users.spi.repository.UsersRewardRepository;
import org.waldreg.reward.users.spi.tag.RewardTagExistChecker;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DefaultUsersRewardManager.class)
public class UsersRewardManagerTest{

    @Autowired
    private UsersRewardManager usersRewardManager;

    @MockBean
    private UsersRewardRepository usersRewardRepository;

    @MockBean
    private RewardTagExistChecker rewardTagExistChecker;

    @MockBean
    private UserExistChecker userExistChecker;

    @Test
    @DisplayName("특정 유저에게 상점 부여 성공 테스트")
    public void ASSIGN_REWARD_TO_USER_TEST(){
        // given
        int id = 1;
        int rewardTagId = 1;

        // when
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(true);
        Mockito.when(rewardTagExistChecker.isRewardTagExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> usersRewardManager.assignRewardToUser(id, rewardTagId));
    }

    @Test
    @DisplayName("특정 유저에게 상점 부여 실패 테스트 - reward tag id를 찾을 수 없음")
    public void ASSIGN_REWARD_TO_USER_FAIL_CANNOT_FIND_REWARD_TAG_ID_TEST(){
        // given
        int id = 1;
        int rewardTagId = 1;

        // when
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(true);
        Mockito.when(rewardTagExistChecker.isRewardTagExist(Mockito.anyInt())).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownRewardTagException.class, () -> usersRewardManager.assignRewardToUser(id, rewardTagId));
    }

    @Test
    @DisplayName("특정 유저에게 상점 부여 실패 테스트 - id에 해당하는 유저를 찾을 수 없음")
    public void ASSIGN_REWARD_TO_USER_FAIL_CANNOT_FIND_USER_BY_ID_TEST(){
        // given
        int id = 1;
        int rewardTagId = 1;

        // when
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(false);
        Mockito.when(rewardTagExistChecker.isRewardTagExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(UnknownRewardTargetException.class, () -> usersRewardManager.assignRewardToUser(id, rewardTagId));
    }

    @Test
    @DisplayName("특정 유저의 상점 목록 조회 성공 테스트")
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
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(true);
        Mockito.when(usersRewardRepository.readSpecifyUsersReward(1)).thenReturn(usersRewardDto);
        UsersRewardDto result = usersRewardManager.readSpecifyUsersReward(1);

        // then
        Assertions.assertEquals(id, result.getId());
    }

    @Test
    @DisplayName("특정 유저의 상점 목록 조회 실패 테스트 - 유저를 찾을 수 없음")
    public void READ_SPECIFY_USERS_REWARD_TAG_FAIL_CANNOT_FIND_USER_TEST(){
        // given
        int id = 1;

        // when
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownRewardTargetException.class, ()-> usersRewardManager.readSpecifyUsersReward(id));
    }

    @Test
    @DisplayName("특정 유저에게 부여된 상 벌점 삭제 성공 테스트")
    public void DELETE_REWARD_TAG_ASSIGNED_TO_SPECIFY_USERS_SUCCESS_TEST(){
        // given
        int id = 1;
        int rewardId = 1;

        // when
        Mockito.when(usersRewardRepository.isRewardIdExist(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertDoesNotThrow(() -> usersRewardManager.deleteRewardToUser(id, rewardId));
    }

    @Test
    @DisplayName("특정 유저에게 부여된 상 벌점 삭제 실패 테스트 - 상점을 삭제할 대상 유저를 찾을 수 없음")
    public void DELETE_REWARD_TAG_ASSIGNED_TO_SPECIFY_USERS_FAIL_CANNOT_FIND_USER_TEST(){
        // given
        int id = 1;
        int rewardId = 1;

        // when
        Mockito.when(usersRewardRepository.isRewardIdExist(Mockito.anyInt(), Mockito.anyInt())).thenReturn(true);
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(false);

        // then
        Assertions.assertThrows(UnknownRewardTargetException.class, () -> usersRewardManager.deleteRewardToUser(id, rewardId));
    }

    @Test
    @DisplayName("특정 유저에게 부여된 상 벌점 삭제 실패 테스트 - 유저가 reward id에 해당하는 상점을 갖고있지 않음")
    public void DELETE_REWARD_TAG_ASSIGNED_TO_SPECIFY_USERS_FAIL_CANNOT_NOT_FINA_REWARD_TEST(){
        // given
        int id = 1;
        int rewardId = 1;

        // when
        Mockito.when(usersRewardRepository.isRewardIdExist(Mockito.anyInt(), Mockito.anyInt())).thenReturn(false);
        Mockito.when(userExistChecker.isUserExist(Mockito.anyInt())).thenReturn(true);

        // then
        Assertions.assertThrows(UnknownRewardException.class, () -> usersRewardManager.deleteRewardToUser(id, rewardId));
    }

    @Test
    @DisplayName("모든 유저의 상점 초기화 성공 테스트")
    public void RESET_ALL_USERS_REWARD_SUCCESS_TEST(){
        // when & then
        Assertions.assertDoesNotThrow(() -> usersRewardManager.resetAllUsersReward());
    }

}