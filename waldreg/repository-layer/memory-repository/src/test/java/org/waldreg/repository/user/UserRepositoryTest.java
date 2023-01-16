package org.waldreg.repository.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={MemoryUserRepository.class, MemoryUserStorage.class})
public class UserRepositoryTest{

    @Autowired
    UserRepository userRepository;

    @Autowired
    MemoryUserStorage memoryUserStorage;

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
        Assertions.assertThrows(DuplicatedUserIdException.class,()->userRepository.createUser(userDto2));

    }

}
