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
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.character.dto.CharacterDto;
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
    private void DELETE_ALL_CHARACTER() { memoryCharacterStorage.deleteAllCharacter();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){
        memoryUserStorage.deleteAllUser();
    }

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_BOARD(){memoryBoardStorage.deleteAllBoard();}

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
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
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
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryDto(categoryDto)
                .build();

        //when
        BoardDto result = boardRepository.createBoard(boardRequest);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryDto().getCategoryName(), result.getCategoryDto().getCategoryName()),
                () -> Assertions.assertSame(result.getCreatedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(0, result.getViews())
        );
    }

    @Test
    @DisplayName("아이디로 게시글 조회 성공 테스트")
    public void INQUIRY_BOARD_BY_ID_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .build();
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
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
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryDto(categoryDto)
                .build();

        //when
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryDto().getCategoryName(), result.getCategoryDto().getCategoryName()),
                () -> Assertions.assertEquals(boardRequest.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getLastModifiedAt(), result.getLastModifiedAt()),
                () -> Assertions.assertEquals(boardRequest.getCommentList(), result.getCommentList()),
                () -> Assertions.assertEquals(boardRequest.getReactions().getReactionMap(), result.getReactions().getReactionMap()),
                () -> Assertions.assertEquals(boardRequest.getViews(), result.getViews())
        );

    }

}
