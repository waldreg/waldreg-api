package org.waldreg.repository.user;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.character.Character;
import org.waldreg.repository.user.mapper.UserRepositoryMapper;
import org.waldreg.repository.user.repository.JpaCharacterRepository;
import org.waldreg.repository.user.repository.JpaUserRepository;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.UserRepository;
import org.waldreg.user.spi.UsersCharacterRepository;

@DataJpaTest
@ContextConfiguration(classes = {UserRepositoryMapper.class,
        UsersCharacterRepository.class,
        UserRepositoryServiceProvider.class,
        UserRepositoryCommander.class,
        JpaUserTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
class UserRepositoryTest{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Test
    @DisplayName("새로운 User 생성 성공 테스트")
    void CREATE_NEW_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);

        //then
        Assertions.assertDoesNotThrow(() -> userRepository.createUser(userDto));

    }

    @Test
    @DisplayName("user_id로 User 조회 성공 테스트")
    void READ_USER_BY_USER_ID_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        UserDto result = userRepository.readUserByUserId(userDto.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(userDto.getName(), result.getName()),
                () -> Assertions.assertEquals(userDto.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(userDto.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(userDto.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(userDto.getCharacter(), result.getCharacter())
        );

    }

    @Test
    @DisplayName("id로 User 조회 성공 테스트")
    void READ_USER_BY_ID_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        UserDto userDto2 = userRepository.readUserByUserId(userDto.getUserId());
        UserDto result = userRepository.readUserById(userDto2.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(userDto2.getName(), result.getName()),
                () -> Assertions.assertEquals(userDto2.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(userDto2.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(userDto2.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(userDto2.getCharacter(), result.getCharacter())
        );

    }

    @Test
    @DisplayName("User 전체 조회 성공 테스트")
    void READ_ALL_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto userDto2 = UserDto.builder()
                .name("alcuk2")
                .userId("alcuk_id2")
                .userPassword("alcuk_pwd22!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto userDto3 = UserDto.builder()
                .name("alcuk3")
                .userId("alcuk_id3")
                .userPassword("alcuk_pwd23!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        int startIdx = 1;
        int endIdx = 2;

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        userRepository.createUser(userDto2);
        userRepository.createUser(userDto3);
        List<UserDto> result = userRepository.readUserList(startIdx, endIdx);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(userDto.getName(), result.get(0).getName()),
                () -> Assertions.assertEquals(userDto.getUserId(), result.get(0).getUserId()),
                () -> Assertions.assertEquals(userDto.getUserPassword(), result.get(0).getUserPassword()),
                () -> Assertions.assertEquals(userDto.getPhoneNumber(), result.get(0).getPhoneNumber()),
                () -> Assertions.assertEquals(userDto.getCharacter(), result.get(0).getCharacter()),
                () -> Assertions.assertEquals(userDto2.getName(), result.get(1).getName()),
                () -> Assertions.assertEquals(userDto2.getUserId(), result.get(1).getUserId()),
                () -> Assertions.assertEquals(userDto2.getUserPassword(), result.get(1).getUserPassword()),
                () -> Assertions.assertEquals(userDto2.getPhoneNumber(), result.get(1).getPhoneNumber()),
                () -> Assertions.assertEquals(userDto2.getCharacter(), result.get(1).getCharacter())
        );

    }

    @Test
    @DisplayName("User 수정 성공 테스트")
    void UPDATE_USER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto modifiedUserDto = UserDto.builder()
                .name("alcuk2")
                .userPassword("alcuk_pwd22!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        UserDto originUserDto = userRepository.readUserByUserId(userDto.getUserId());
        userRepository.updateUser(originUserDto.getId(), modifiedUserDto);
        UserDto result = userRepository.readUserByUserId(userDto.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(originUserDto.getId(), result.getId()),
                () -> Assertions.assertEquals(modifiedUserDto.getName(), result.getName()),
                () -> Assertions.assertEquals(originUserDto.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(modifiedUserDto.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(modifiedUserDto.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(originUserDto.getCharacter(), result.getCharacter()),
                () -> Assertions.assertEquals(originUserDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(originUserDto.getRewardPoint(), result.getRewardPoint())
        );

    }

    @Test
    @DisplayName("User 역할 수정 성공 테스트")
    void UPDATE_USER_CHARACTER_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        Character modifiedCharacter = Character.builder()
                .characterName("modifiedCharacter")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        jpaCharacterRepository.save(modifiedCharacter);
        userRepository.createUser(userDto);
        UserDto originUserDto = userRepository.readUserByUserId(userDto.getUserId());
        userRepository.updateCharacter(originUserDto.getId(), modifiedCharacter.getCharacterName());
        UserDto result = userRepository.readUserByUserId(userDto.getUserId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(originUserDto.getId(), result.getId()),
                () -> Assertions.assertEquals(originUserDto.getName(), result.getName()),
                () -> Assertions.assertEquals(originUserDto.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(originUserDto.getUserPassword(), result.getUserPassword()),
                () -> Assertions.assertEquals(originUserDto.getPhoneNumber(), result.getPhoneNumber()),
                () -> Assertions.assertEquals(modifiedCharacter.getCharacterName(), result.getCharacter()),
                () -> Assertions.assertEquals(originUserDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(originUserDto.getRewardPoint(), result.getRewardPoint())
        );

    }

    @Test
    @DisplayName("id로 User 삭제 성공 테스트")
    void DELETE_USER_BY_ID_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        UserDto userDto2 = userRepository.readUserByUserId(userDto.getUserId());
        userRepository.deleteById(userDto2.getId());

        //then
        Assertions.assertFalse(() -> userRepository.isExistId(userDto2.getId()));

    }

    @Test
    @DisplayName("User 특정 목록 조회 성공 테스트")
    void READ_SPECIFIC_USER_LIST_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        UserDto userDto = UserDto.builder()
                .name("alcuk")
                .userId("alcuk_id")
                .userPassword("alcuk_pwd2!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto userDto2 = UserDto.builder()
                .name("alcuk2")
                .userId("alcuk_id2")
                .userPassword("alcuk_pwd22!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        UserDto userDto3 = UserDto.builder()
                .name("alcuk3")
                .userId("alcuk_id3")
                .userPassword("alcuk_pwd23!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();

        //when
        jpaCharacterRepository.save(character);
        userRepository.createUser(userDto);
        userRepository.createUser(userDto2);
        userRepository.createUser(userDto3);
        int id1 = userRepository.readUserByUserId("alcuk_id").getId();
        int id2 = userRepository.readUserByUserId("alcuk_id3").getId();
        List<UserDto> result = userRepository.readSpecificUserList(List.of(id1,id2));

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(userDto.getName(), result.get(0).getName()),
                () -> Assertions.assertEquals(userDto.getUserId(), result.get(0).getUserId()),
                () -> Assertions.assertEquals(userDto.getUserPassword(), result.get(0).getUserPassword()),
                () -> Assertions.assertEquals(userDto.getPhoneNumber(), result.get(0).getPhoneNumber()),
                () -> Assertions.assertEquals(userDto.getCharacter(), result.get(0).getCharacter()),
                () -> Assertions.assertEquals(userDto3.getName(), result.get(1).getName()),
                () -> Assertions.assertEquals(userDto3.getUserId(), result.get(1).getUserId()),
                () -> Assertions.assertEquals(userDto3.getUserPassword(), result.get(1).getUserPassword()),
                () -> Assertions.assertEquals(userDto3.getPhoneNumber(), result.get(1).getPhoneNumber()),
                () -> Assertions.assertEquals(userDto3.getCharacter(), result.get(1).getCharacter())
        );

    }

}
