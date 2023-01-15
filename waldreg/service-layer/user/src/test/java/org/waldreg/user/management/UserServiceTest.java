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
    @DisplayName("유저 보안 조회 성공 테스트")
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

}
