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
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryUserRepository.class, MemoryUserStorage.class, UserMapper.class})
public class UserRepositoryTest{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemoryUserStorage memoryUserStorage;

    @MockBean
    MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL(){
        memoryUserStorage.deleteAllUser();
    }

    @Test
    @DisplayName("유저 생성 성공 테스트")
    public void CREATE_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> userRepository.createUser(userDto));
    }

    @Test
    @DisplayName("유저 생성 실패 테스트 - 중복 user id")
    public void CREATE_USER_FAIL_CAUSE_DUPLICATED_USER_ID_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId("linirini_id")
                .name("linirini2")
                .userPassword("linirini_pwd2")
                .phoneNumber("010-1234-2222")
                .build();

        //when
        userRepository.createUser(userDto);

        //then
        Assertions.assertThrows(DuplicatedUserIdException.class, () -> userRepository.createUser(userDto2));
    }

    @Test
    @DisplayName("특정 유저 조회 성공 테스트")
    public void READ_SPECIFIC_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        userRepository.createUser(userDto);
        UserDto result = userRepository.readUserByUserId(userDto.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getName(), userDto.getName()),
                () -> Assertions.assertEquals(result.getUserId(), userDto.getUserId()),
                () -> Assertions.assertEquals(result.getUserPassword(), userDto.getUserPassword()),
                () -> Assertions.assertEquals(result.getUserPassword(), userDto.getUserPassword())
        );
    }

    @Test
    @DisplayName("특정 유저 조회 실패 테스트 - 없는 user id")
    public void READ_SPECIFIC_USER_FAIL_UNKNOWN_USER_ID_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();
        String wrongUserId = "wrong";

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        userRepository.createUser(userDto);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> userRepository.readUserByUserId(wrongUserId));

    }

}
