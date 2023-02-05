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
    private void DELETE_ALL_CHARACTER(){memoryCharacterStorage.deleteAllCharacter();}

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
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryDto.getId())
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(filePathList)
                .imageUrls(imagePathList)
                .build();

        //when
        Assertions.assertDoesNotThrow(() -> boardRepository.createBoard(boardRequest));
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
        boardRepository.createBoard(boardRequest);
        List<BoardDto> boardDtoList = boardRepository.searchByTitle(title);
        BoardDto result = boardRepository.inquiryBoardById(boardDtoList.get(0).getId());

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
        List<BoardDto> result = boardRepository.searchByTitle(title);

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
        List<BoardDto> result = boardRepository.searchByContent("This");

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
        List<BoardDto> result = boardRepository.searchByAuthorUserId(userDto2.getUserId());

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
        List<BoardDto> result = boardRepository.inquiryAllBoardByCategory(categoryDto.getId(), 1,1);

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
        List<BoardDto> result = boardRepository.inquiryAllBoard(1,2);

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

        //when
        boardRepository.createBoard(boardRequest);
        boardRepository.createBoard(boardRequest2);
        List<BoardDto> boardDtoList = boardRepository.searchByTitle(title);
        boardRepository.deleteBoard(boardDtoList.get(0).getId());
        List<BoardDto> result = boardRepository.searchByTitle(title);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, result.size()),
                () -> Assertions.assertEquals(boardRequest2.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardRequest2.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardRequest2.getUserDto().getName(), result.get(0).getUserDto().getName()),
                () -> Assertions.assertEquals(boardRequest2.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertNotNull(result.get(0).getCreatedAt()),
                () -> Assertions.assertEquals(boardRequest2.getFileUrls().get(0), result.get(0).getFileUrls().get(0)),
                () -> Assertions.assertEquals(boardRequest2.getImageUrls().get(0), result.get(0).getImageUrls().get(0)),
                () -> Assertions.assertNotNull(result.get(0).getLastModifiedAt()),
                () -> Assertions.assertTrue(result.get(0).getViews() == 0)
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
                .build();
        String modifiedTitle = "modifiedTitle";
        String modifiedContent = "hihihi";

        //when
        boardRepository.createBoard(boardRequest);
        BoardDto boardDto = boardRepository.searchByTitle(title).get(0);
        System.out.println("!!"+boardDto.getId());
        BoardDto modifiedBoardRequest = BoardDto.builder()
                .id(boardDto.getId())
                .title(modifiedTitle)
                .content(modifiedContent)
                .userDto(boardDto.getUserDto())
                .createdAt(boardDto.getCreatedAt())
                .categoryId(boardDto.getCategoryId())
                .fileUrls(List.of())
                .imageUrls(imagePathList)
                .reactions(boardDto.getReactions())
                .commentList(boardDto.getCommentList())
                .views(boardDto.getViews())
                .build();
        boardRepository.modifyBoard(modifiedBoardRequest);
        BoardDto result = boardRepository.inquiryBoardById(boardDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(modifiedBoardRequest.getId(), result.getId()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getContent(), result.getContent()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertEquals(modifiedBoardRequest.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertTrue(result.getFileUrls() == null),
                () -> Assertions.assertEquals(modifiedBoardRequest.getImageUrls().get(0), result.getImageUrls().get(0)),
                () -> Assertions.assertTrue(boardDto.getLastModifiedAt().isBefore(result.getLastModifiedAt())),
                () -> Assertions.assertEquals(modifiedBoardRequest.getViews(), result.getViews())
        );

    }


}
