package org.waldreg.user.management;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.InvalidNameException;
import org.waldreg.user.exception.InvalidPhoneNumberException;
import org.waldreg.user.exception.InvalidUserIdException;
import org.waldreg.user.exception.UnsecuredPasswordException;
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


}
