package org.waldreg.repository.board;

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
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryCommentStorage;
import org.waldreg.repository.MemoryUserStorage;
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
@ContextConfiguration(classes = {MemoryCommentStorage.class, MemoryCommentRepository.class, CategoryMapper.class, MemoryCategoryStorage.class, MemoryCategoryRepository.class, CommentMapper.class, MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class})
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
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");

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
        BoardDto result = boardRepository.createBoard(boardRequest);
        categoryRepository.addBoardInCategoryBoardList(result);
        CategoryDto boardInCategoryResult = categoryRepository.inquiryCategoryById(categoryId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(boardInCategoryResult.getBoardDtoList().size() == 1),
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertNotNull(result.getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.getLastModifiedAt()),
                () -> Assertions.assertTrue(result.getViews() == 0)
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
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
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
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertNotNull(result.getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.getLastModifiedAt()),
                () -> Assertions.assertTrue(result.getViews() == 0)
        );

    }

    @Test
    @DisplayName("제목으로 게시글 목록 조회 성공 테스트")
    public void INQUIRY_BOARD_LIST_BY_TITLE_SUCCESS_TEST(){
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
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title2 = "title2";
        BoardDto boardRequest2 = BoardDto.builder()
                .title(title2)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title3 = "hihihi";
        BoardDto boardRequest3 = BoardDto.builder()
                .title(title3)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        boardRepository.createBoard(boardRequest3);
        List<BoardDto> result = boardRepository.searchByTitle(title, 1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(boardRequest2.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest2.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest2.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest2.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest2.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0),
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.get(1).getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.get(1).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.get(1).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(1).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.get(1).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.get(1).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(1).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(1).getViews() == 0)
        );

    }

    @Test
    @DisplayName("content로 게시글 목록 조회 성공 테스트")
    public void INQUIRY_BOARD_LIST_BY_CONTENT_SUCCESS_TEST(){
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
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        String title = "title";
        String content = "content";
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title2 = "title2";
        String content2 = "This is content2!";
        BoardDto boardRequest2 = BoardDto.builder()
                .title(title2)
                .userDto(userDto)
                .content(content2)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title3 = "hihihi";
        String content3 = "This is content3!";
        BoardDto boardRequest3 = BoardDto.builder()
                .title(title3)
                .userDto(userDto)
                .content(content3)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        boardRepository.createBoard(boardRequest3);
        List<BoardDto> result = boardRepository.searchByContent("This", 1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(boardRequest2.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest2.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest2.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest2.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest2.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0),
                () -> Assertions.assertEquals(boardRequest3.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardRequest3.getContent(), result.get(1).getContent()),
                () -> Assertions.assertEquals(boardRequest3.getUserDto().getUserId(), result.get(1).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest3.getUserDto().getName(), result.get(1).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest3.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(1).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest3.getFileUrls().get(0), result.get(1).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest3.getImageUrls().get(0), result.get(1).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(1).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(1).getViews() == 0)
        );

    }

    @Test
    @DisplayName("Author의 user_id로 게시글 목록 조회 성공 테스트")
    public void INQUIRY_BOARD_LIST_BY_AUTHOR_USER_ID_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .build();
        org.waldreg.user.dto.UserDto user2 = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id2")
                .name("alcuk2")
                .userPassword("alcuk1233!")
                .phoneNumber("010-1234-3333")
                .build();
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
                .build();
        characterRepository.createCharacter(characterDto);
        userRepository.createUser(user);
        userRepository.createUser(user2);
        org.waldreg.user.dto.UserDto userResponse = userRepository.readUserByUserId(user.getUserId());
        org.waldreg.user.dto.UserDto userResponse2 = userRepository.readUserByUserId(user2.getUserId());
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(userResponse2.getId())
                .userId("alcuk_id2")
                .name("alcuk2")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        String title = "title";
        String content = "content";
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto2)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title2 = "title2";
        String content2 = "This is content2!";
        BoardDto boardRequest2 = BoardDto.builder()
                .title(title2)
                .userDto(userDto)
                .content(content2)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title3 = "hihihi";
        String content3 = "This is content3!";
        BoardDto boardRequest3 = BoardDto.builder()
                .title(title3)
                .userDto(userDto)
                .content(content3)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        boardRepository.createBoard(boardRequest3);
        List<BoardDto> result = boardRepository.searchByAuthorUserId(userDto2.getUserId(), 1, 1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0)
        );

    }

    @Test
    @DisplayName("카테고리로 게시글 목록 조회 성공 테스트")
    public void INQUIRY_ALL_BOARD_LIST_BY_CATEGORY_SUCCESS_TEST(){
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
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(2)
                .categoryName("cate2")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title2 = "title2";
        BoardDto boardRequest2 = BoardDto.builder()
                .title(title2)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title3 = "hihihi";
        BoardDto boardRequest3 = BoardDto.builder()
                .title(title3)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto2.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        boardRepository.createBoard(boardRequest3);
        List<BoardDto> result = boardRepository.inquiryAllBoardByCategory(categoryDto.getId(), 1, 1);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0)
        );

    }

    @Test
    @DisplayName("전체 게시글 목록 조회 성공 테스트")
    public void INQUIRY_ALL_BOARD_LIST_SUCCESS_TEST(){
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
        List<String> fileUrlList = new ArrayList<>();
        fileUrlList.add("uuid.pptx");
        List<String> imageUrlList = new ArrayList<>();
        imageUrlList.add("uuid.png");
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(1)
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title2 = "title2";
        BoardDto boardRequest2 = BoardDto.builder()
                .title(title2)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();
        String title3 = "hihihi";
        BoardDto boardRequest3 = BoardDto.builder()
                .title(title3)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .fileUrls(filePathList)
                .imageUrls(imageUrlList)
                .build();

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        boardRepository.createBoard(boardRequest3);
        List<BoardDto> result = boardRepository.inquiryAllBoard(1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, result.size()),
                () -> Assertions.assertEquals(boardRequest.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0),
                () -> Assertions.assertEquals(boardRequest2.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardRequest2.getContent(), result.get(1).getContent()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getUserId(), result.get(1).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getName(), result.get(1).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest2.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(1).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest2.getFileUrls().get(0), result.get(1).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest2.getImageUrls().get(0), result.get(1).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(1).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(1).getViews() == 0)
        );

    }

    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    public void DELETE_BOARD_BY_ID_SUCCESS_TEST(){
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
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        CommentDto commentRequest = CommentDto.builder()
                .boardId(boardDto.getId())
                .userDto(userDto)
                .content(content)
                .build();
        CommentDto commentDto = commentRepository.createComment(commentRequest);
        commentInBoardRepository.addCommentInBoardCommentList(commentDto);
        boardRepository.deleteBoard(boardDto.getId());
        CategoryDto boardInCategoryResult = categoryRepository.inquiryCategoryById(categoryId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(boardRepository.isExistBoard(boardDto.getId())),
                () -> Assertions.assertTrue(boardInCategoryResult.getBoardDtoList().size() == 0),
                () -> Assertions.assertFalse(commentRepository.isExistComment(commentDto.getId()))
        );

    }

    @Test
    @DisplayName("게시글 수정 성공 테스트")
    public void MODIFY_BOARD_SUCCESS_TEST(){
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
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        BoardDto modifiedBoardRequest = BoardDto.builder()
                .id(boardDto.getId())
                .title("changedtitle")
                .content("blablablablabla")
                .userDto(boardDto.getUserDto())
                .createdAt(boardDto.getCreatedAt())
                .lastModifiedAt(boardDto.getLastModifiedAt().plusMinutes(3))
                .categoryId(boardDto.getCategoryId())
                .fileUrls(List.of())
                .imageUrls(imagePathList)
                .reactions(boardDto.getReactions())
                .commentList(boardDto.getCommentList())
                .views(boardDto.getViews())
                .build();
        boardRepository.modifyBoard(modifiedBoardRequest);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());
        BoardDto boardInCategory = categoryRepository.inquiryCategoryById(categoryId).getBoardDtoList().get(0);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardInCategory.getId(), result.getId()),
                () -> Assertions.assertEquals(boardInCategory.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardInCategory.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardInCategory.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardInCategory.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardInCategory.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertEquals(boardInCategory.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertTrue(result.getFileUrls().size() == 0),
                () -> Assertions.assertEquals(boardInCategory.getImageUrls().get(0), result.getImageUrls().get(0)),
                () -> Assertions.assertTrue(boardDto.getLastModifiedAt().isBefore(result.getLastModifiedAt())),
                () -> Assertions.assertEquals(boardInCategory.getViews(), result.getViews())
        );

    }


}
