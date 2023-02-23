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
@ContextConfiguration(classes = {MemoryJoiningPoolRepository.class, MemoryUserRepository.class, MemoryUserStorage.class,MemoryJoiningPoolStorage.class, UserMapper.class})
public class UserRepositoryTest{

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
                .character("Guest")
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.approveJoin(userDto.getUserId());
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
    @DisplayName("전체 유저 조회 성공 테스트")
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
        joiningPoolRepository.approveJoin(userDto.getUserId());
        joiningPoolRepository.approveJoin(userDto2.getUserId());
        joiningPoolRepository.approveJoin(userDto3.getUserId());

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
                .character("Guest")
                .build();
        String updateCharacter = "updateCharacter";

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Guest"))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());

        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.approveJoin(userDto.getUserId());
        UserDto userDto1 = userRepository.readUserByUserId(userDto.getUserId());

        Mockito.when(memoryCharacterStorage.readCharacterByName(updateCharacter))
                .thenReturn(Character.builder().characterName(updateCharacter).permissionList(List.of()).build());

        userRepository.updateCharacter(userDto1.getId(), updateCharacter);
        UserDto result = userRepository.readUserByUserId(userDto1.getUserId());

        //then
        Assertions.assertEquals(updateCharacter, result.getCharacter());
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
                .character("Guest")
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName(Mockito.anyString()))
                .thenReturn(Character.builder().characterName("Guest").permissionList(List.of()).build());
        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.approveJoin(userDto.getUserId());
        UserDto userDto1 = userRepository.readUserByUserId(userDto.getUserId());
        userRepository.deleteById(userDto1.getId());

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> userRepository.readUserByUserId(userDto1.getUserId()));

    }

}
