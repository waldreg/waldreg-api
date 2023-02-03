package org.waldreg.repository.board;

import java.util.ArrayList;
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
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceMemberTier;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class})
public class BoardRepositoryTest{

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemoryBoardStorage memoryBoardStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL(){
        memoryUserStorage.deleteAllUser();
    }

    @Test
    @DisplayName("새로운 보드 생성 성공 테스트")
    public void CREATE_NEW_BOARD_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .build();
        PermissionDto permissionDto = PermissionDto.builder()
                .id(1)
                .name("TIER_3")
                .status("true")
                .build();
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        permissionDtoList.add(permissionDto);
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(permissionDtoList)
                .build();
        characterRepository.createCharacter(characterDto);
        userRepository.createUser(user);
        org.waldreg.user.dto.UserDto userResponse = userRepository.readUserByUserId("alcuk_id");
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .memberTier(BoardServiceMemberTier.TIER_3)
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .memberTier(BoardServiceMemberTier.TIER_3)
                .build();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryDto(categoryDto)
                .boardServiceMemberTier(BoardServiceMemberTier.TIER_3)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> boardRepository.createBoard(boardRequest));

    }


}
