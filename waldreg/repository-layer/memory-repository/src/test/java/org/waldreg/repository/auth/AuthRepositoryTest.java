package org.waldreg.repository.auth;


import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryCommentStorage;
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

    @MockBean
    private MemoryCharacterStorage memoryCharacterStorage;

    @MockBean
    private MemoryBoardStorage memoryBoardStorage;
    @MockBean
    private MemoryCommentStorage memoryCommentStorage;

    @BeforeEach
    public void CLEAR_memoryUserStorage(){

    }

    @Test
    @DisplayName("유저 id, password 로 조회 성공 테스트")
    public void READ_USER_BY_USERID_USERPASSWORD_TEST(){
        //given
        String userId = "Guest";
        String userPassword = "12345";
        User user = User.builder()
                .name("asdfg")
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Guest")).thenReturn(Character.builder()
                                                                                             .characterName("Guest")
                                                                                             .permissionList(List.of())
                                                                                             .build());
        memoryUserStorage.createUser(user);
        TokenUserDto foundUser = memoryAuthRepository.findUserByUserIdPassword(user.getUserId(), user.getUserPassword());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword())
        );
    }

    @Test
    @DisplayName("유저 조회 실패 password 불일치 테스트")
    public void READ_USER_BY_USERID_USERPASSWORD_FAIL_TEST(){
        //given
        String userId = "Guest";
        String userPassword = "12345";
        String inputUserId = "bbb";
        String inputUserPassword = "1234";
        User user = User.builder()
                .name("bbb")
                .userId(inputUserId)
                .userPassword(userPassword)
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Guest")).thenReturn(Character.builder()
                                                                                             .characterName("Guest")
                                                                                             .permissionList(List.of())
                                                                                             .build());
        memoryUserStorage.createUser(user);

        //then
        Assertions.assertThrows(PasswordMissMatchException.class, () -> memoryAuthRepository.findUserByUserIdPassword(inputUserId, inputUserPassword));
    }

    @Test
    @DisplayName("id로 유저 조회 테스트")
    public void READ_USER_BY_ID_TEST(){
        //given
        String userId = "Admin";
        String userPassword = "12345";
        User user = User.builder()
                .name("Admin")
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Admin")).thenReturn(Character.builder()
                                                                                             .characterName("Admin")
                                                                                             .permissionList(List.of())
                                                                                             .build());
        memoryUserStorage.createUser(user);
        TokenUserDto foundUser = memoryAuthRepository.findUserById(user.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword())
        );
    }

    @Test
    @DisplayName("boardId 로 인증 테스트")
    public void READ_USER_BY_BOARD_ID_TEST(){
        //given
        String userId = "Admin";
        String userPassword = "12345";
        User user = User.builder()
                .name("Admin")
                .userId(userId)
                .userPassword(userPassword)
                .build();

        //when
        Mockito.when(memoryCharacterStorage.readCharacterByName("Admin")).thenReturn(
                Character.builder()
                        .characterName("Admin")
                        .permissionList(List.of())
                        .build());

        memoryUserStorage.createUser(user);
        int boardId = 1;
        Mockito.when(memoryBoardStorage.inquiryBoardById(boardId)).thenReturn(
                Board.builder()
                        .user(user)
                        .id(boardId)
                        .title("title")
                        .content("content")
                        .build()
        );

        TokenUserDto foundUser = memoryAuthRepository.findUserByBoardId(boardId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword())
        );
    }

}
