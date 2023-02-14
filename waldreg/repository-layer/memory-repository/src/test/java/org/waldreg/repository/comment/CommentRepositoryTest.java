package org.waldreg.repository.comment;

import java.time.LocalDateTime;
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
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryCommentStorage;
import org.waldreg.repository.MemoryJoiningPoolStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.board.BoardMapper;
import org.waldreg.repository.board.MemoryBoardRepository;
import org.waldreg.repository.boarduserinfo.UserInfoMapper;
import org.waldreg.repository.category.CategoryMapper;
import org.waldreg.repository.category.MemoryCategoryRepository;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryJoiningPoolRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.JoiningPoolRepository;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserInfoMapper.class, MemoryCategoryRepository.class, MemoryCategoryStorage.class, CategoryMapper.class, MemoryCommentRepository.class, MemoryCommentStorage.class, CommentMapper.class, MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class, MemoryJoiningPoolRepository.class, MemoryJoiningPoolStorage.class})
public class CommentRepositoryTest{

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemoryBoardStorage memoryBoardStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @Autowired
    MemoryJoiningPoolStorage memoryJoiningPoolStorage;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemoryCategoryStorage memoryCategoryStorage;

    @Autowired
    private CommentInBoardRepository commentInBoardRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemoryCommentStorage memoryCommentStorage;

    @Autowired
    private JoiningPoolRepository joiningPoolRepository;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_CHARACTER(){memoryCharacterStorage.deleteAllCharacter();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){
        memoryUserStorage.deleteAllUser();
    }

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_BOARD(){memoryBoardStorage.deleteAllBoard();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_CATEGORY(){memoryCategoryStorage.deleteAllCategory();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_COMMENT(){memoryCommentStorage.deleteAllComment();}

    @Test
    @DisplayName("새로운 댓글 생성 성공 테스트")
    public void CREATE_NEW_COMMENT_SUCCESS_TEST(){
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
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(1)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();

        //when
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        CommentDto commentRequest = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content(content)
                .build();
        CommentDto commentDto = commentRepository.createComment(commentRequest);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(commentRequest.getBoardId(), result.getCommentList().get(0).getBoardId()),
                () -> Assertions.assertEquals(commentRequest.getUserDto().getId(), result.getCommentList().get(0).getUserDto().getId()),
                () -> Assertions.assertEquals(commentRequest.getContent(), result.getCommentList().get(0).getContent())
        );

    }

    @Test
    @DisplayName("댓글 전체 조회 성공")
    public void INQUIRY_ALL_COMMENT_SUCCESS_TEST(){
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
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(1)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();
        BoardDto boardRequest2 = BoardDto.builder()
                .title("holholholhol")
                .userDto(userDto)
                .content(content)
                .categoryId(2)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();

        //when
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        BoardDto boardDto2 = boardRepository.createBoard(boardRequest2);
        CommentDto commentRequest = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content(content)
                .build();
        CommentDto commentRequest2 = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content("comcomcomcom")
                .build();
        CommentDto commentRequest3 = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content("metnemtnemtent")
                .build();
        CommentDto commentRequest4 = CommentDto.builder()
                .boardId(boardDto2.getId())
                .userDto(userDto)
                .content("wowowowowwowow")
                .build();
        CommentDto commentDto = commentRepository.createComment(commentRequest);
        CommentDto commentDto2 = commentRepository.createComment(commentRequest2);
        CommentDto commentDto3 = commentRepository.createComment(commentRequest3);
        CommentDto commentDto4 = commentRepository.createComment(commentRequest4);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto2);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto3);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto4);
        List<CommentDto> result = commentRepository.inquiryAllCommentByBoardId(boardDto.getId(), 1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(commentDto.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(commentDto.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(commentDto.getCreatedAt(), result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(commentDto.getLastModifiedAt(), result.get(0).getLastModifiedAt()),
                () -> Assertions.assertEquals(commentDto.getBoardId(), result.get(0).getBoardId()),
                () -> Assertions.assertEquals(commentDto.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(commentDto2.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(commentDto2.getUserDto().getUserId(), result.get(1).getUserDto().getUserId()),
                () -> Assertions.assertEquals(commentDto2.getCreatedAt(), result.get(1).getCreatedAt()),
                () -> Assertions.assertEquals(commentDto2.getLastModifiedAt(), result.get(1).getLastModifiedAt()),
                () -> Assertions.assertEquals(commentDto2.getBoardId(), result.get(1).getBoardId()),
                () -> Assertions.assertEquals(commentDto2.getContent(), result.get(1).getContent())
        );

    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    public void MODIFY_COMMENT_SUCCESS_TEST(){
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
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(1)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();

        //when
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        CommentDto commentRequest = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content(content)
                .build();
        CommentDto commentDto = commentRepository.createComment(commentRequest);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto);
        CommentDto modifiedCommentDto = CommentDto.builder()
                .id(commentDto.getId())
                .boardId(boardDto.getId())
                .userDto(commentDto.getUserDto())
                .content("fixfixfixfixfix")
                .createdAt(commentDto.getCreatedAt())
                .lastModifiedAt(LocalDateTime.now().plusMinutes(3))
                .build();
        commentRepository.modifyComment(modifiedCommentDto);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(modifiedCommentDto.getId(), result.getCommentList().get(0).getId()),
                () -> Assertions.assertEquals(modifiedCommentDto.getBoardId(), result.getCommentList().get(0).getBoardId()),
                () -> Assertions.assertEquals(modifiedCommentDto.getUserDto().getId(), result.getCommentList().get(0).getUserDto().getId()),
                () -> Assertions.assertEquals(modifiedCommentDto.getContent(), result.getCommentList().get(0).getContent()),
                () -> Assertions.assertEquals(modifiedCommentDto.getUserDto().getUserId(), result.getCommentList().get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(modifiedCommentDto.getCreatedAt(), result.getCommentList().get(0).getCreatedAt()),
                () -> Assertions.assertEquals(modifiedCommentDto.getLastModifiedAt(), result.getCommentList().get(0).getLastModifiedAt())
        );
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    public void DELETE_COMMENT_SUCCESS_TEST(){
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
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(1)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();

        //when
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        CommentDto commentRequest = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content(content)
                .build();
        CommentDto commentDto = commentRepository.createComment(commentRequest);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto);
        commentRepository.deleteComment(commentDto.getId());
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(() -> commentRepository.isExistComment(commentDto.getId())),
                () -> Assertions.assertTrue(result.getCommentList().size() == 0)
        );

    }


}
