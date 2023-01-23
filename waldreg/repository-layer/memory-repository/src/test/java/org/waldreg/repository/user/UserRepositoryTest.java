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
import org.waldreg.user.exception.UnknownIdException;
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
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
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
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
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

    @Test
    @DisplayName("전체 유저 조회 성공 테스트")
    public void READ_ALL_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId("linirini_id2")
                .name("linirini2")
                .userPassword("linirini_pwd2")
                .phoneNumber("010-1234-2222")
                .build();
        UserDto userDto3 = UserDto.builder()
                .userId("linirini_id3")
                .name("linirini3")
                .userPassword("linirini_pwd3")
                .phoneNumber("010-1234-3333")
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        userRepository.createUser(userDto);
        userRepository.createUser(userDto2);
        userRepository.createUser(userDto3);
        int maxIdx = userRepository.readMaxIdx();
        List<UserDto> result = userRepository.readUserList(1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.get(0).getUserId(), userDto2.getUserId()),
                () -> Assertions.assertEquals(result.get(1).getUserId(), userDto.getUserId()),
                () -> Assertions.assertEquals(maxIdx, 3)
        );
    }

    @Test
    @DisplayName("유저 역할 수정 성공 테스트")
    public void UPDATE_USER_CHARACTER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();
        String updateCharacter = "updateCharacter";

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Guest"))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());

        userRepository.createUser(userDto);
        UserDto userDto1 = userRepository.readUserByUserId(userDto.getUserId());

        Mockito.when(memoryCharacterStorage.readCharacterByName(updateCharacter))
                .thenReturn(Character.builder().characterName(updateCharacter).permissionList(List.of()).build());

        userRepository.updateCharacter(userDto1.getId(), updateCharacter);
        UserDto result = userRepository.readUserByUserId(userDto1.getUserId());

        //then
        Assertions.assertEquals(updateCharacter, result.getCharacter());
    }

    @Test
    @DisplayName("유저 역할 수정 실패 테스트 - 없는 id")
    public void UPDATE_USER_CHARACTER_FAIL_CAUSE_UNKNOWN_ID_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("linirini_id")
                .name("linirini")
                .userPassword("linirini_pwd")
                .phoneNumber("010-1234-1234")
                .build();
        String updateCharacter = "updateCharacter";
        int id = 0;

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        userRepository.createUser(userDto);

        //then
        Assertions.assertThrows(UnknownIdException.class, () -> userRepository.updateCharacter(id, updateCharacter));
    }

    @Test
    @DisplayName("유저 삭제 테스트")
    public void DELETE_USER_ONLINE_SUCCESS_TEST(){
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
        UserDto userDto1 = userRepository.readUserByUserId(userDto.getUserId());
        userRepository.deleteById(userDto1.getId());

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> userRepository.readUserByUserId(userDto1.getUserId()));

    }

}
