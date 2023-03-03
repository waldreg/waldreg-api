package org.waldreg.repository.joiningpool;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.repository.joiningpool.mapper.JoiningPoolMapper;
import org.waldreg.repository.joiningpool.repository.JpaJoiningPoolRepository;
import org.waldreg.repository.joiningpool.repository.JpaUserRepository;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.spi.JoiningPoolRepository;

@DataJpaTest
@ContextConfiguration(classes = {JoiningPoolRepositoryServiceProvider.class,
        JpaUserRepository.class, JpaJoiningPoolRepository.class,JoiningPoolMapper.class,
        JpaJoiningPoolTestInitializer.class
})
@TestPropertySource("classpath:h2-application.properties")
public class JoiningPoolTest{

    @Autowired
    private JoiningPoolRepository joiningPoolRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaJoiningPoolRepository jpaJoiningPoolRepository;

    @Test
    @DisplayName("가입 신청 성공 테스트")
    public void CREATE_JOINING_POOL_USER_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        //when&then
        Assertions.assertDoesNotThrow(() -> joiningPoolRepository.createUser(userDto));
    }

    @Test
    @DisplayName("가입 신청한 사람들의 수 조회")
    public void GET_JOINING_POOL_USER_COUNT_TEST(){
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId("userId2")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        UserDto userDto3 = UserDto.builder()
                .userId("userId3")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.createUser(userDto2);
        joiningPoolRepository.createUser(userDto3);

        //when
        int count = joiningPoolRepository.readJoiningPoolMaxIdx();
        //then
        Assertions.assertEquals(3,count);
    }

    @Test
    @DisplayName("가입 신청한 사람들 목록 조회 - from to")
    public void GET_JOINING_POOL_USER_LIST_TEST(){
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        UserDto userDto2 = UserDto.builder()
                .userId("userId2")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        UserDto userDto3 = UserDto.builder()
                .userId("userId3")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        joiningPoolRepository.createUser(userDto);
        joiningPoolRepository.createUser(userDto2);
        joiningPoolRepository.createUser(userDto3);

        //when
        List<UserDto> userDtoList = joiningPoolRepository.readUserJoiningPool(0,2);
        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(3,userDtoList.size()),
                ()->Assertions.assertEquals(userDto.getUserId(),userDtoList.get(0).getUserId()),
                ()->Assertions.assertEquals(userDto2.getUserId(),userDtoList.get(1).getUserId()),
                ()->Assertions.assertEquals(userDto3.getUserId(),userDtoList.get(2).getUserId())
        );
    }

    @Test
    @DisplayName("유저 아이디로 조회 성공")
    public void INQUIRY_USER_BY_USER_ID_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        joiningPoolRepository.createUser(userDto);
        //when
        UserDto result = joiningPoolRepository.getUserByUserId(userDto.getUserId());
        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(userDto.getUserId(),result.getUserId()),
                ()->Assertions.assertEquals(userDto.getUserPassword(),result.getUserPassword()),
                ()->Assertions.assertEquals(userDto.getName(),result.getName()),
                ()->Assertions.assertEquals(userDto.getPhoneNumber(),result.getPhoneNumber())
        );
    }

    @Test
    @DisplayName("유저 아이디로 존재 여부 조회 성공")
    public void IS_EXIST_USER_BY_USER_ID_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        joiningPoolRepository.createUser(userDto);
        //when
        boolean result = joiningPoolRepository.isExistUserId(userDto.getUserId());
        //then
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("대기열에서 userId로 유저 삭제 성공 테스트")
    public void DELETE_USER_BY_USER_ID_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .userId("userId")
                .name("name")
                .userPassword("userPassword")
                .phoneNumber("010-1234-5678")
                .build();
        joiningPoolRepository.createUser(userDto);
        //when
        joiningPoolRepository.deleteUserByUserId(userDto.getUserId());

        //then
        Assertions.assertFalse(joiningPoolRepository.isExistUserId(userDto.getUserId()));

    }



}
