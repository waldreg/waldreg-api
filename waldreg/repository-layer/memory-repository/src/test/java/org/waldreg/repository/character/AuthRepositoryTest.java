package org.waldreg.repository.character;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.auth.MemoryAuthRepository;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.PasswordMissMatchException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryAuthRepository.class, MemoryUserStorage.class})
public class AuthRepositoryTest{

    @Autowired
    private MemoryAuthRepository memoryAuthRepository;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @BeforeEach
    public void CLEAR_memoryUserStorage(){

    }

    @Test
    @DisplayName("유저 id, password 로 조회 성공 테스트")
    public void READ_USER_BY_USERID_USERPASSWORD_TEST(){
        //given
        String userId = "asdfg";
        String userPassword = "12345";
        User user = User.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        memoryUserStorage.createUser(user);
        TokenUserDto foundUser = memoryAuthRepository.findUserByUserIdPassword(user.getUserId(), user.getUserPassword());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword())
        );
    }

    @Test
    @DisplayName("유저 조회 실패 password 불일치 테스트")
    public void READ_USER_BY_USERID_USERPASSWORD_FAIL_TEST(){
        //given
        String userId = "bbb";
        String userPassword = "12345";
        User user = User.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();
        String inputUserId = "bbb";
        String inputUserPassword = "1234";

        //when
        memoryUserStorage.createUser(user);

        //then
        Assertions.assertThrows(PasswordMissMatchException.class,()-> memoryAuthRepository.findUserByUserIdPassword(inputUserId, inputUserPassword));
    }

    @Test
    @DisplayName("id로 유저 조회 테스트")
    public void READ_USER_BY_ID_TEST(){
        //given
        String userId = "abc";
        String userPassword = "12345";
        User user = User.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        memoryUserStorage.createUser(user);
        TokenUserDto foundUser = memoryAuthRepository.findUserById(user.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword())
        );
    }


}
