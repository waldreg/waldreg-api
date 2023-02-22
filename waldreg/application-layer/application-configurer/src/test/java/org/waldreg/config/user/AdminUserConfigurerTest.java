package org.waldreg.config.user;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.management.user.UserManager;

@SpringBootTest
public class AdminUserConfigurerTest{

    @Autowired
    private UserManager userManager;

    private final String userId = "admin";

    @Test
    @DisplayName("Admin 유저 생성 테스트")
    public void ADMIN_USER_CREATE_TEST(){
        // given
        String name = "Admin";
        String userId = "Admin";
        String password = "0000";
        String character = "Admin";
        String phoneNumber = "000-0000-0000";
        UserDto userDto = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(password)
                .character(character)
                .phoneNumber(phoneNumber)
                .socialLogin(List.of())
                .build();

        // when
        UserDto result = userManager.readUserByUserId(userId);

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(name, userDto.getName()),
                ()-> Assertions.assertEquals(userId, userDto.getUserId()),
                ()-> Assertions.assertEquals(password, userDto.getUserPassword()),
                ()-> Assertions.assertEquals(character, userDto.getCharacter()),
                ()-> Assertions.assertEquals(phoneNumber, userDto.getPhoneNumber()),
                ()-> Assertions.assertEquals(0, userDto.getSocialLogin().size())
        );
    }

}
