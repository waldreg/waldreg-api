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
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.management.CharacterManager;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.management.joiningpool.DefaultJoiningPoolManager;
import org.waldreg.user.management.joiningpool.JoiningPoolManager;
import org.waldreg.user.management.user.DefaultUserManager;
import org.waldreg.user.management.user.UserManager;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;
import org.waldreg.user.spi.UsersCharacterRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultUserManager.class, DefaultJoiningPoolManager.class})
public class UserServiceTest{

    @MockBean
    private CharacterManager characterManager;

    @Autowired
    private UserManager userManager;

    @Autowired
    private JoiningPoolManager joiningPoolManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JoiningPoolRepository joiningPoolRepository;
    @MockBean
    private UsersCharacterRepository userCharacterRepository;

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
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);

        // when & then
        Assertions.assertDoesNotThrow(() -> joiningPoolManager.createUser(createRequest));

    }

    @Test
    @DisplayName("유저 생성 실패 테스트 - 가입된 유저 목록에 중복 user id")
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
        Mockito.when(userRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicatedUserIdException.class, () -> joiningPoolManager.createUser(userDto2));
    }

    @Test
    @DisplayName("유저 생성 실패 테스트 - 가입 대기열에 중복 user id")
    public void CREATE_USER_FAIL_CAUSE_DUPLICATED_USER_ID_INJOINING_POOL_TEST(){
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
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicatedUserIdException.class, () -> joiningPoolManager.createUser(userDto2));
    }

    @Test
    @DisplayName("가입 대기열 조회 성공 테스트")
    public void READ_USER_JOINING_POOL_SUCCESS_TEST(){
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
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.createUser(createRequest);
        userDtoList.add(createRequest);
        joiningPoolManager.createUser(createRequest2);
        userDtoList.add(createRequest2);
        joiningPoolManager.createUser(createRequest3);
        userDtoList.add(createRequest3);
        int maxIdx = userDtoList.size();
        Mockito.when(joiningPoolRepository.readJoiningPoolMaxIdx()).thenReturn(maxIdx);
        Mockito.when(joiningPoolRepository.readUserJoiningPool(Mockito.anyInt(), Mockito.anyInt())).thenReturn(userDtoList);
        List<UserDto> result = joiningPoolManager.readUserJoiningPool(stIdx, enIdx);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(maxIdx, result.size()),
                () -> Assertions.assertEquals(createRequest.getName(), result.get(0).getName()),
                () -> Assertions.assertEquals(createRequest2.getName(), result.get(1).getName()),
                () -> Assertions.assertEquals(createRequest3.getName(), result.get(2).getName())
        );
    }

    @Test
    @DisplayName("가입 대기열 조회 실패 테스트 - 잘못된 범위")
    public void READ_USER_JOINING_POOL_INVALID_RANGE_TEST(){
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
        int stIdx = 3;
        int enIdx = 1;

        //when
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.createUser(createRequest);
        userDtoList.add(createRequest);
        joiningPoolManager.createUser(createRequest2);
        userDtoList.add(createRequest2);
        joiningPoolManager.createUser(createRequest3);
        userDtoList.add(createRequest3);
        int maxIdx = userDtoList.size();
        Mockito.when(joiningPoolRepository.readJoiningPoolMaxIdx()).thenReturn(maxIdx);
        Mockito.when(joiningPoolRepository.readUserJoiningPool(Mockito.anyInt(), Mockito.anyInt())).thenReturn(userDtoList);

        //then
        Assertions.assertThrows(InvalidRangeException.class, () -> joiningPoolManager.readUserJoiningPool(stIdx, enIdx));
    }

    @Test
    @DisplayName("유저 가입 승인 테스트")
    public void APPROVE_USER_JOIN_SUCCESS_TEST(){
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
        List<UserDto> joiningPoolList = new ArrayList<>();
        joiningPoolList.add(createRequest);
        //when
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> joiningPoolManager.approveJoin(joiningPoolList.get(0).getUserId()));

    }

    @Test
    @DisplayName("유저 가입 승인 실패 - 없는 유저 아이디")
    public void APPROVE_USER_JOIN_FAIL_UNKNOWN_USER_ID_TEST(){
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
        List<UserDto> joiningPoolList = new ArrayList<>();
        joiningPoolList.add(createRequest);
        //when
        //when
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> joiningPoolManager.approveJoin(joiningPoolList.get(0).getUserId()));

    }

    @Test
    @DisplayName("유저 가입 거절 성공 테스트")
    public void REJECT_USER_JOIN_SUCCESS_TEST(){
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
        List<UserDto> joiningPoolList = new ArrayList<>();
        joiningPoolList.add(createRequest);
        //when
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> joiningPoolManager.rejectJoin(joiningPoolList.get(0).getUserId()));

    }

    @Test
    @DisplayName("유저 가입 거절 실패 - 없는 유저 아이디")
    public void REJECT_USER_JOIN_FAIL_UNKNOWN_USER_ID_TEST(){
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
        List<UserDto> joiningPoolList = new ArrayList<>();
        joiningPoolList.add(createRequest);
        //when
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> joiningPoolManager.rejectJoin(joiningPoolList.get(0).getUserId()));

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
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
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
        Mockito.when(userRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.readUserByUserId(Mockito.anyString())).thenReturn(createRequest);
        UserDto result = userManager.readUserByUserId(createRequest.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(createRequest.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(createRequest.getName(), result.getName()),
                () -> Assertions.assertEquals(createRequest.getPhoneNumber(), result.getPhoneNumber())
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
        Mockito.when(userRepository.isExistUserId(Mockito.anyString())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownUserIdException.class, () -> userManager.readUserByUserId(wrongUserId));

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
        userDtoList.add(createRequest);
        userDtoList.add(createRequest2);
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
        int startIdx = 3;
        int endIdx = 1;
        int maxIdx = 2;
        List<UserDto> userDtoList = new ArrayList<>();

        //when
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
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
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

    @Test
    @DisplayName("유저 역할 수정 성공 테스트")
    public void UPDATE_USER_CHARACTER_SUCCESS_TEST(){
        //given
        String name = "홍길동";
        String userId = "hello123";
        String userPassword = "hello1234";
        String phoneNumber = "010-1234-1234";
        UserDto createRequest = UserDto.builder()
                .id(1)
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .build();
        String updateCharacter = "update_character";
        UserDto createCharacterRequest = UserDto.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber(phoneNumber)
                .character(updateCharacter)
                .build();

        //when
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        Mockito.when(userRepository.readUserByUserId(Mockito.anyString())).thenReturn(createRequest);
        UserDto origin = userManager.readUserByUserId(createRequest.getUserId());
        userManager.updateCharacter(origin.getId(), updateCharacter);
        Mockito.when(userRepository.readUserByUserId(Mockito.anyString())).thenReturn(createCharacterRequest);
        UserDto result = userManager.readUserByUserId(origin.getUserId());

        //then
        Assertions.assertEquals(result.getCharacter(), updateCharacter);
    }

    @Test
    @DisplayName("유저 역할 수정 실패 테스트 - Admin 역할 수정 불가")
    public void UPDATE_USER_CHARACTER_FAIL_CAUSE_ADMIN_TEST(){
        //given
        String updateCharacter = "update_character";
        UserDto userDto = UserDto.builder()
                .userId("Admin")
                .build();

        //when
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(userDto);

        //then
        Assertions.assertThrows(NoPermissionException.class,() -> userManager.updateCharacter(1,updateCharacter));
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
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(userDto);
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(false);

        //then
        Assertions.assertThrows(UnknownIdException.class, () -> userManager.updateCharacter(id, updateCharacter));
    }

    @Test
    @DisplayName("유저 삭제 실패 테스트 - Admin 삭제 불가")
    public void DELETE_USER_CHARACTER_FAIL_CAUSE_ADMIN_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("Admin")
                .build();

        //when
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(userDto);

        //then
        Assertions.assertThrows(NoPermissionException.class,() -> userManager.deleteById(1));
    }

    @Test
    @DisplayName("유저 셀프 삭제 성공 테스트")
    public void DELETE_USER_OWN_SUCCESS_TEST(){
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
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.createUser(createRequest);
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.approveJoin(createRequest.getUserId());
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertDoesNotThrow(() -> userManager.deleteById(id));
    }

    @Test
    @DisplayName("유저 강제 퇴장 성공 테스트")
    public void DELETE_OTHER_USER_SUCCESS_TEST(){
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
        Mockito.when(userCharacterRepository.isExistCharacterName(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.createUser(createRequest);
        Mockito.when(joiningPoolRepository.isExistUserId(Mockito.anyString())).thenReturn(true);
        joiningPoolManager.approveJoin(createRequest.getUserId());
        Mockito.when(userRepository.isExistId(Mockito.anyInt())).thenReturn(true);
        Mockito.when(userRepository.readUserById(Mockito.anyInt())).thenReturn(createRequest);
        //then
        Assertions.assertDoesNotThrow(() -> userManager.deleteById(id));
    }

}