package org.waldreg.repository.user;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.domain.character.Character;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryJoiningPoolStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryUserRepository.class, MemoryJoiningPoolRepository.class, MemoryJoiningPoolStorage.class, MemoryUserStorage.class, UserMapper.class})
public class JoiningPoolRepositoryTest{

    @Autowired
    UserRepository userRepository;

    @Autowired
    JoiningPoolRepository joiningPoolRepository;

    @Autowired
    MemoryUserStorage memoryUserStorage;

    @Autowired
    MemoryJoiningPoolStorage memoryJoiningPoolStorage;

    @MockBean
    MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL(){
        memoryUserStorage.deleteAllUser();
        memoryJoiningPoolStorage.deleteAllUser();
    }

    @Test
    @DisplayName("가입 신청 성공 테스트")
    public void CREATE_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when&then
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        Assertions.assertDoesNotThrow(() -> joiningPoolRepository.createUser(userDto));
    }

    @Test
    @DisplayName("가입 신청 승인 테스트")
    public void APPROVE_USER_JOIN_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when&then
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        joiningPoolRepository.createUser(userDto);
        Assertions.assertDoesNotThrow(() -> joiningPoolRepository.approveJoin(userDto.getUserId()));

    }
    @Test
    @DisplayName("가입 신청 거절 테스트")
    public void REJECT_USER_JOIN_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when&then
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        joiningPoolRepository.createUser(userDto);
        Assertions.assertDoesNotThrow(() -> joiningPoolRepository.rejectJoin(userDto.getUserId()));

    }

    @Test
    @DisplayName("가입 신청 대기열 전체조회 성공 테스트")
    public void READ_ALL_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId("linirini_id2")
                .name("linirini2")
                .userPassword("linirini_pwd2")
                .phoneNumber("010-1234-2222")
                .character("Guest")
                .build();
        UserDto userDto3 = UserDto.builder()
                .userId("linirini_id3")
                .name("linirini3")
                .userPassword("linirini_pwd3")
                .phoneNumber("010-1234-3333")
                .character("Guest")
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.createUser(userDto2);
        joiningPoolRepository.createUser(userDto3);
        int maxIdx = joiningPoolRepository.readJoiningPoolMaxIdx();
        List<UserDto> result = joiningPoolRepository.readUserJoiningPool(1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.get(0).getUserId(), userDto.getUserId()),
                () -> Assertions.assertEquals(result.get(1).getUserId(), userDto2.getUserId()),
                () -> Assertions.assertEquals(maxIdx, 3)
        );
    }

}
