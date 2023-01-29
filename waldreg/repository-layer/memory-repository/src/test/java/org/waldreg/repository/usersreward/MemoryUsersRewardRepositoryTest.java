package org.waldreg.repository.usersreward;

import java.util.ArrayList;
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
import org.waldreg.domain.character.Character;
import org.waldreg.domain.rewardtag.RewardTag;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryRewardTagStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.reward.MemoryRewardTagRepository;
import org.waldreg.repository.reward.RewardTagMapper;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryUsersRewardRepository.class,
        MemoryRewardTagRepository.class,
        RewardTagMapper.class,
        MemoryRewardTagStorage.class,
        MemoryUserStorage.class,
        MemoryCharacterStorage.class})
public class MemoryUsersRewardRepositoryTest{

    @Autowired
    private MemoryUsersRewardRepository memoryUsersRewardRepository;

    @Autowired
    private MemoryRewardTagStorage memoryRewardTagStorage;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    public void INIT_ALL(){
        memoryRewardTagStorage.deleteAll();
        memoryUserStorage.deleteAllUser();
        memoryCharacterStorage.deleteAllCharacter();
        memoryCharacterStorage.createCharacter(Character.builder().characterName("Guest").permissionList(List.of()).build());
    }

    @Test
    @DisplayName("특정 유저에게 상점 부여 테스트")
    public void ASSIGN_REWARD_TAG_TO_USER_TEST(){
        // given
        int userId = createUserAndGetId();
        int rewardTagId = createRewardTagAndGetId();

        // when & then
        Assertions.assertDoesNotThrow(() -> memoryUsersRewardRepository.assignRewardToUser(userId, rewardTagId));
    }

    private int createUserAndGetId(){
        User user = User.builder()
                .userId("test user")
                .name("test user")
                .phoneNumber("010-1234-5678")
                .userPassword("helloworld!@#123")
                .socialLogin(new ArrayList<>())
                .build();
        memoryUserStorage.createUser(user);
        return memoryUserStorage.readUserByUserId("test user").getId();
    }

    private int createRewardTagAndGetId(){
        RewardTag rewardTag = RewardTag.builder()
                .rewardTagTitle("test reward")
                .rewardPoint(10)
                .build();
        memoryRewardTagStorage.createRewardTag(rewardTag);
        return memoryRewardTagStorage.readRewardTagList().get(0).getRewardTagId();
    }

}
