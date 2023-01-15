package org.waldreg.user.management;

import java.util.ArrayList;
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
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultUserManager.class})
public class UserServiceTest{

    @Autowired
    private UserManager userManager;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 생성 성공 테스트")
    public void CREATE_USER_SUCCESS_TEST(){
        // given
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        // when & then
        Assertions.assertDoesNotThrow(() -> userManager.createUser(createRequest));

    }

    @Test
    @DisplayName("토큰에 해당하는 유저 조회 성공 테스트")
    public void READ_USER_ONLINE_SUCCESS_TEST(){
        //given
        int id = 1;
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        userManager.createUser(createRequest);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        UserDto result = userManager.readUserById(id);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(createRequest.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(createRequest.getName(), result.getName()),
                () -> Assertions.assertEquals(createRequest.getPhoneNumber(), result.getPhoneNumber())
        );

    }

    @Test
    @DisplayName("특정 유저 조회 성공 테스트")
    public void READ_SPECIFIC_USER_SUCCESS_TEST(){
        //given
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();

        //when
        userManager.createUser(createRequest);

        Mockito.when(userRepository.readUserByName(Mockito.anyString())).thenReturn(createRequest);
        UserDto result = userManager.readUserByName(createRequest.getName());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(createRequest.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(createRequest.getName(), result.getName()),
                () -> Assertions.assertEquals(createRequest.getPhoneNumber(), result.getPhoneNumber())
        );

    }

    @Test
    @DisplayName("전체 유저 조회 성공 테스트")
    public void READ_ALL_USER_SUCCESS_TEST(){
        //given
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String name2 = "홍길동2";
        String userId2 = "hello1234";
        String userPassword2 = "hello12345";
        String phoneNumber2 = "010-1234-1111";
        UserDto createRequest2 = UserDto.builder()
                .name(name2)
                .userId(userId2)
                .userPassword(userPassword2)
                .phoneNumber(phoneNumber2)
                .build();
        String name3 = "홍길동3";
        String userId3 = "hello12356";
        String userPassword3 = "hello123456";
        String phoneNumber3 = "010-1234-333";
        UserDto createRequest3 = UserDto.builder()
                .name(name3)
                .userId(userId3)
                .userPassword(userPassword3)
                .phoneNumber(phoneNumber3)
                .build();
        List<UserDto> userDtoList = new ArrayList<>();
        int stIdx = 1;
        int enIdx = 3;

        //when
        userManager.createUser(createRequest);
        userDtoList.add(createRequest);
        userManager.createUser(createRequest2);
        userDtoList.add(createRequest2);
        userManager.createUser(createRequest3);
        userDtoList.add(createRequest3);
        int maxIdx = userDtoList.size();
        Mockito.when(userRepository.readMaxIdx()).thenReturn(maxIdx);
        Mockito.when(userRepository.readUserList(Mockito.anyInt(), Mockito.anyInt())).thenReturn(userDtoList);
        List<UserDto> result = userManager.readUserList(stIdx, enIdx);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(maxIdx, result.size()),
                () -> Assertions.assertEquals(createRequest.getName(), result.get(0).getName()),
                () -> Assertions.assertEquals(createRequest2.getName(), result.get(1).getName()),
                () -> Assertions.assertEquals(createRequest3.getName(), result.get(2).getName())
        );

    }

    @Test
    @DisplayName("전체 유저 조회 실패 테스트 - 잘못된 범위 지정")
    public void READ_ALL_USER_FAIL_CAUSE_INVALID_RANGE_TEST(){
        //given
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        int startIdx = 1;
        int endIdx = 3;
        int maxIdx = 2;
        List<UserDto> userDtoList = new ArrayList<>();

        //when
        userManager.createUser(createRequest);
        userDtoList.add(createRequest);
        Mockito.when(userRepository.readMaxIdx()).thenReturn(maxIdx);

        //then
        Assertions.assertThrows(InvalidRangeException.class, () -> userManager.readUserList(startIdx, endIdx));

    }

    @Test
    @DisplayName("특정 유저 수정 성공 테스트")
    public void UPDATE_SPECIFIC_USER_SUCCESS_TEST(){
        //given
        int id = 1;
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String updateName = "짱구";
        String updatePhoneNumber = "010-4321-1234";
        UserDto createUpdateRequest = UserDto.builder()
                .name(updateName)
                .phoneNumber(updatePhoneNumber)
                .build();
        UserDto changedRequest = UserDto.builder()
                .name(updateName)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(updatePhoneNumber)
                .build();

        //when
        userManager.createUser(createRequest);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        UserDto result = userManager.readUserById(id);
        userManager.updateUser(result.getId(), createUpdateRequest);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(changedRequest);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getId(), changedRequest.getId()),
                () -> Assertions.assertEquals(changedRequest.getName(), updateName),
                () -> Assertions.assertEquals(changedRequest.getPhoneNumber(), updatePhoneNumber)
        );


    }

}
