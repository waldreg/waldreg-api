package org.waldreg.repository.boarduserinfo;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.spi.BoardUserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.reaction.spi.ReactionUserRepository;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.domain.board.Board;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryJoiningPoolStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryJoiningPoolRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserInfoMapper.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, UserMapper.class, CharacterMapper.class, MemoryBoardUserInfoRepository.class, MemoryJoiningPoolRepository.class, MemoryJoiningPoolStorage.class})
public class BoardUserInfoRepositoryTest{

    @Autowired
    private BoardUserRepository boardUserRepository;

    @Autowired
    private ReactionUserRepository reactionUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;
    @Autowired
    private JoiningPoolRepository joiningPoolRepository;

    @Autowired
    MemoryJoiningPoolStorage memoryJoiningPoolStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_CHARACTER(){memoryCharacterStorage.deleteAllCharacter();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){
        memoryUserStorage.deleteAllUser();
    }

    @Test
    @DisplayName("id로 유저 정보 조회 성공 테스트")
    public void GET_USER_INFO_BY_ID_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
                .build();
        characterRepository.createCharacter(characterDto);
        joiningPoolRepository.createUser(user);
        joiningPoolRepository.approveJoin(user.getUserId());
        org.waldreg.user.dto.UserDto userResponse = userRepository.readUserByUserId("alcuk_id");

        //when
        int id = userResponse.getId();
        UserDto result = boardUserRepository.getUserInfo(id);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(userResponse.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(userResponse.getName(), result.getName()),
                () -> Assertions.assertEquals(userResponse.getId(),result.getId())
        );

    }

    @Test
    @DisplayName("userId로 유저 정보 조회 성공 테스트")
    public void GET_USER_INFO_BY_USER_ID_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .character("Guest")
                .build();
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
                .build();
        characterRepository.createCharacter(characterDto);
        joiningPoolRepository.createUser(user);
        joiningPoolRepository.approveJoin(user.getUserId());
        org.waldreg.user.dto.UserDto userResponse = userRepository.readUserByUserId("alcuk_id");

        //when
        String userId = userResponse.getUserId();
        UserDto result = reactionUserRepository.getUserInfoByUserId(userId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(userResponse.getUserId(), result.getUserId()),
                () -> Assertions.assertEquals(userResponse.getName(), result.getName()),
                () -> Assertions.assertEquals(userResponse.getId(),result.getId())
        );

    }


}
