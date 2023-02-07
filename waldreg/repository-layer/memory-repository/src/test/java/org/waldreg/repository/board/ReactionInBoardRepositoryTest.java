package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryCommentStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.boarduserinfo.UserInfoMapper;
import org.waldreg.repository.category.CategoryMapper;
import org.waldreg.repository.category.MemoryCategoryRepository;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.comment.CommentMapper;
import org.waldreg.repository.comment.MemoryCommentRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserInfoMapper.class, MemoryCommentStorage.class, MemoryCommentRepository.class, CategoryMapper.class, MemoryCategoryStorage.class, MemoryCategoryRepository.class, CommentMapper.class, MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class})
public class ReactionInBoardRepositoryTest{

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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemoryCategoryStorage memoryCategoryStorage;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentInBoardRepository commentInBoardRepository;

    @Autowired
    private MemoryCommentStorage memoryCommentStorage;

    @Autowired
    private ReactionInBoardRepository reactionInBoardRepository;

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

    @Test
    @DisplayName("reaction 추가 성공 테스트")
    public void ADD_NEW_BOARD_SUCCESS_TEST(){
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
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());
        reactionMap.get(BoardServiceReactionType.BAD).add(userDto);

        //when
        categoryRepository.createCategory(categoryDto);
        int categoryId = categoryRepository.inquiryAllCategory().get(0).getId();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryId)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(filePathList)
                .imageUrls(imagePathList)
                .build();
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        ReactionDto reactionDto = ReactionDto.builder()
                .boardId(boardDto.getId())
                .reactionMap(reactionMap)
                .build();
        reactionInBoardRepository.storeReactionDto(reactionDto);
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).size() == 1),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.SAD)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.HEART)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.SMILE)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.GOOD)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.CHECK)),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getId(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getId()),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getName(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getName()),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getUserId(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getUserId())
        );
    }

    @Test
    @DisplayName("reaction 조회 성공 테스트")
    public void GET_NEW_BOARD_SUCCESS_TEST(){
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
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());
        reactionMap.get(BoardServiceReactionType.BAD).add(userDto);

        //when
        categoryRepository.createCategory(categoryDto);
        int categoryId = categoryRepository.inquiryAllCategory().get(0).getId();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryId)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(filePathList)
                .imageUrls(imagePathList)
                .build();
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        ReactionDto reactionDto = ReactionDto.builder()
                .boardId(boardDto.getId())
                .reactionMap(reactionMap)
                .build();
        reactionInBoardRepository.storeReactionDto(reactionDto);
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).size() == 1),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.SAD)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.HEART)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.SMILE)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.GOOD)),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getReactions().getReactionMap().get(BoardServiceReactionType.CHECK)),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getId(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getId()),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getName(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getName()),
                () -> Assertions.assertEquals(reactionDto.getReactionMap().get(BoardServiceReactionType.BAD).get(0).getUserId(), result.getReactions().getReactionMap().get(BoardServiceReactionType.BAD).get(0).getUserId())
        );
    }

}
