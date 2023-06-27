package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.BoardRepositoryMapper;
import org.waldreg.repository.board.mapper.CategoryRepositoryMapper;
import org.waldreg.repository.board.mapper.CommentRepositoryMapper;
import org.waldreg.repository.board.mapper.ReactionRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaReactionRepository;
import org.waldreg.repository.board.repository.JpaReactionUserRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {BoardRepositoryServiceProvider.class, CategoryRepositoryServiceProvider.class, CommentRepositoryServiceProvider.class, ReactionRepositoryServiceProvider.class,
        BoardRepositoryMapper.class, CategoryRepositoryMapper.class, ReactionRepositoryMapper.class, CommentRepositoryMapper.class,
        JpaCharacterRepository.class, JpaCategoryRepository.class, JpaUserRepository.class, JpaReactionRepository.class, JpaReactionUserRepository.class,
        BoardCommander.class, CommentCommander.class, FileNameCommander.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
class BoardRepositoryTest{

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private BoardRepositoryMapper boardRepositoryMapper;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    private void INIT(){
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 게시글 생성 테스트")
    public void CREATE_NEW_BOARD_SUCCESS_TEST(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        jpaCharacterRepository.save(character);
        User user = User.builder()
                .userId("Fixtar")
                .name("123")
                .userPassword("abcd")
                .phoneNumber("01012345678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        Category category = Category.builder()
                .categoryName("cate1")
                .build();
        jpaCategoryRepository.save(category);
        Integer categoryId = jpaCategoryRepository.findAll().get(0).getId();

        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardDto = BoardDto.builder()
                .title("boardTitle")
                .content("boardContent")
                .userDto(boardRepositoryMapper.userToUserDto(user))
                .categoryId(categoryId)
                .imageUrls(imagePathList)
                .fileUrls(filePathList)
                .build();

        BoardDto result = boardRepository.createBoard(boardDto);

        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardDto.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardDto.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardDto.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertEquals(boardDto.getImageUrls(), result.getImageUrls()),
                () -> Assertions.assertEquals(boardDto.getFileUrls(), result.getFileUrls()),
                () -> Assertions.assertEquals(6, result.getReactions().getReactionMap().size())
        );
    }

    @Test
    @DisplayName("아이디로 게시글 조회 성공 테스트")
    void INQUIRY_BOARD_BY_ID_TEST(){
        //given
        BoardDto boardDto = setDefaultBoard();
        //when
        Integer boardId = boardDto.getId();

        BoardDto result = boardRepository.inquiryBoardById(boardId);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardDto.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardDto.getUserDto().getName(), result.getUserDto().getName()),
                () -> Assertions.assertEquals(boardDto.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertEquals(boardDto.getImageUrls(), result.getImageUrls()),
                () -> Assertions.assertEquals(boardDto.getFileUrls(), result.getFileUrls())

        );

    }

    @Test
    @DisplayName("전체 게시글 조회 성공 테스트")
    void INQUIRY_ALL_BOARD_BY_FROM_TO_TEST(){
        //given
        setDefaultBoardList();
        //when
        List<BoardDto> boardDtoList = boardRepository.inquiryAllBoard(1, 6);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDtoList.size(), 5),
                () -> Assertions.assertEquals(boardDtoList.get(0).getTitle(), "boardTitle"),
                () -> Assertions.assertEquals(boardDtoList.get(1).getTitle(), "boardTitle2"),
                () -> Assertions.assertEquals(boardDtoList.get(2).getTitle(), "boardTitle3"),
                () -> Assertions.assertEquals(boardDtoList.get(3).getTitle(), "boardTitle4"),
                () -> Assertions.assertEquals(boardDtoList.get(4).getTitle(), "boardTitle5")
        );

    }

    @Test
    @DisplayName("카테고리로 게시글 목록 조회 성공 테스트")
    void INQUIRY_ALL_BOARD_LIST_BY_CATEGORY_SUCCESS_TEST(){
        //given
        setDefaultBoardList();
        Integer categoryId = boardRepository.inquiryAllBoard(1, 6).get(0).getCategoryId();
        //when
        List<BoardDto> foundBoardList = boardRepository.inquiryAllBoardByCategory(categoryId, 1, 6);
        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(foundBoardList.size() == 3),
                () -> Assertions.assertEquals(foundBoardList.get(0).getCategoryId(), categoryId),
                () -> Assertions.assertEquals(foundBoardList.get(1).getCategoryId(), categoryId),
                () -> Assertions.assertEquals(foundBoardList.get(2).getCategoryId(), categoryId)
        );

    }


    @Test
    @DisplayName("제목으로 게시글 목록 조회 성공 테스트")
    void INQUIRY_BOARD_LIST_BY_TITLE_SUCCESS_TEST(){

        //given
        setDefaultBoardList();
        String title = "boardTitle";
        //when
        List<BoardDto> foundBoardList = boardRepository.searchByTitle(title, 1, 6);
        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(foundBoardList.size() == 5),
                () -> Assertions.assertEquals(foundBoardList.get(0).getTitle(), title),
                () -> Assertions.assertEquals(foundBoardList.get(1).getTitle(), "boardTitle2"),
                () -> Assertions.assertEquals(foundBoardList.get(2).getTitle(), "boardTitle3"),
                () -> Assertions.assertEquals(foundBoardList.get(3).getTitle(), "boardTitle4"),
                () -> Assertions.assertEquals(foundBoardList.get(4).getTitle(), "boardTitle5")
        );

    }

    @Test
    @DisplayName("내용으로 게시글 목록 조회 성공 테스트")
    void INQUIRY_BOARD_LIST_BY_CONTENT_SUCCESS_TEST(){
        //given
        setDefaultBoardList();
        String content = "boardContent";
        //when
        List<BoardDto> foundBoardList = boardRepository.searchByContent(content, 1, 6);
        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(foundBoardList.size() == 5),
                () -> Assertions.assertEquals(foundBoardList.get(0).getContent(), content),
                () -> Assertions.assertEquals(foundBoardList.get(1).getContent(), "boardContent2"),
                () -> Assertions.assertEquals(foundBoardList.get(2).getContent(), "boardContent3"),
                () -> Assertions.assertEquals(foundBoardList.get(3).getContent(), "boardContent4"),
                () -> Assertions.assertEquals(foundBoardList.get(4).getContent(), "boardContent5")
        );

    }

    @Test
    @DisplayName("글쓴이의 아이디로 게시글 목록 조회 성공 테스트")
    void INQUIRY_BOARD_LIST_BY_USER_ID_SUCCESS_TEST(){
        //given
        setDefaultBoardList();
        String userId = "Fixtar";
        //when
        List<BoardDto> foundBoardList = boardRepository.searchByAuthorUserId(userId, 1, 6);
        //then
        Assertions.assertAll(
                () -> Assertions.assertTrue(foundBoardList.size() == 3),
                () -> Assertions.assertEquals(foundBoardList.get(0).getUserDto().getUserId(), userId),
                () -> Assertions.assertEquals(foundBoardList.get(1).getUserDto().getUserId(), userId),
                () -> Assertions.assertEquals(foundBoardList.get(2).getUserDto().getUserId(), userId)
        );

    }


    @Test
    @DisplayName("게시글 수정 성공 테스트")
    void MODIFY_BOARD_SUCCESS_TEST(){
        //given
        BoardDto boardDto = setDefaultBoard();
        //when
        Category category = Category.builder()
                .categoryName("modifiedCategory")
                .build();
        jpaCategoryRepository.save(category);
        entityManager.flush();

        boardDto.setContent("modifiedContent");
        boardDto.setTitle("modifiedTitle");
        boardDto.setCategoryId(category.getId());
        boardDto.setLastModifiedAt(LocalDateTime.now());
        boardRepository.modifyBoard(boardDto);

        BoardDto foundBoardDto = boardRepository.inquiryBoardById(boardDto.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getTitle(), foundBoardDto.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(), foundBoardDto.getContent()),
                () -> Assertions.assertEquals(boardDto.getCategoryId(), foundBoardDto.getCategoryId()),
                () -> Assertions.assertEquals(boardDto.getLastModifiedAt(), foundBoardDto.getLastModifiedAt())
        );
    }

    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void DELETE_BOARD_BY_ID_SUCCESS_TEST(){
        //given
        BoardDto boardDto = setDefaultBoard();

        //when
        boardRepository.deleteBoard(boardDto.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(boardRepository.isExistBoard(boardDto.getId())),
                () -> Assertions.assertTrue(jpaUserRepository.existsById(boardDto.getUserDto().getId())),
                () -> Assertions.assertTrue(jpaCategoryRepository.existsById(boardDto.getCategoryId()))
        );
    }

    @Test
    @DisplayName("게시글 존재 여부 확인 테스트")
    void IS_EXIST_SUCCESS_TEST(){
        //given
        BoardDto boardDto = setDefaultBoard();

        //when
        //then
        Assertions.assertTrue(boardRepository.isExistBoard(boardDto.getId()));
    }

    @Test
    @DisplayName("전체 게시글 개수 테스트")
    void GET_MAX_INDEX_TEST(){
        setDefaultBoardList();

        Assertions.assertEquals(boardRepository.getBoardMaxIdx(), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 제목")
    void GET_MAX_INDEX_BY_TITLE_TEST(){
        setDefaultBoardList();
        String title = "boardTitle";
        Assertions.assertEquals(boardRepository.getBoardMaxIdxByTitle(title), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 내용")
    void GET_MAX_INDEX_BY_CONTENT_TEST(){
        setDefaultBoardList();
        String content = "boardContent";
        Assertions.assertEquals(boardRepository.getBoardMaxIdxByContent(content), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 유저 아이디")
    void GET_MAX_INDEX_BY_USER_ID_TEST(){
        setDefaultBoardList();
        String userId = "Fixtar";
        Assertions.assertEquals(boardRepository.getBoardMaxIdxByAuthorUserId(userId), 3);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 카테고리 아이디")
    void GET_MAX_INDEX_BY_CATEGORY_ID_TEST(){
        setDefaultBoardList();

        Integer categoryId = jpaBoardRepository.findAll().get(0).getCategory().getId();

        Assertions.assertEquals(boardRepository.getBoardMaxIdxByCategory(categoryId), 3);
    }


    private BoardDto setDefaultBoard(){
        //given
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();
        jpaCharacterRepository.save(character);
        User user = User.builder()
                .userId("Fixtar")
                .name("123")
                .userPassword("abcd")
                .phoneNumber("01012345678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        Category category = Category.builder()
                .categoryName("cate1")
                .build();
        jpaCategoryRepository.save(category);
        Integer categoryId = jpaCategoryRepository.findAll().get(0).getId();

        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        BoardDto boardDto = BoardDto.builder()
                .title("boardTitle")
                .content("boardContent")
                .userDto(boardRepositoryMapper.userToUserDto(user))
                .categoryId(categoryId)
                .imageUrls(imagePathList)
                .fileUrls(filePathList)
                .build();

        BoardDto result = boardRepository.createBoard(boardDto);
        return result;
    }


    private void setDefaultBoardList(){
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        User user = User.builder()
                .userId("Fixtar")
                .name("123")
                .userPassword("abcd")
                .phoneNumber("01012345678")
                .character(character)
                .build();
        User user2 = User.builder()
                .userId("secondUserId")
                .name("abce")
                .userPassword("abcd")
                .phoneNumber("01012345678")
                .character(character)
                .build();

        Category category = Category.builder()
                .categoryName("cate1")
                .build();

        Category category2 = Category.builder()
                .categoryName("cate2")
                .build();

        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");

        List<Board> boardList = new ArrayList<>();

        boardList.add(Board.builder()
                              .title("boardTitle")
                              .content("boardContent")
                              .user(user)
                              .category(category)
                              .createdAt(LocalDateTime.now())
                              .imagePathList(imagePathList)
                              .filePathList(filePathList)
                              .build()
        );
        boardList.add(Board.builder()
                              .title("boardTitle2")
                              .content("boardContent2")
                              .user(user)
                              .category(category)
                              .createdAt(LocalDateTime.now())
                              .imagePathList(imagePathList)
                              .filePathList(filePathList)
                              .build()
        );
        boardList.add(Board.builder()
                              .title("boardTitle3")
                              .content("boardContent3")
                              .user(user2)
                              .category(category2)
                              .createdAt(LocalDateTime.now())
                              .imagePathList(imagePathList)
                              .filePathList(filePathList)
                              .build()
        );
        boardList.add(Board.builder()
                              .title("boardTitle4")
                              .content("boardContent4")
                              .user(user)
                              .category(category2)
                              .createdAt(LocalDateTime.now())
                              .imagePathList(imagePathList)
                              .filePathList(filePathList)
                              .build()
        );
        boardList.add(Board.builder()
                              .title("boardTitle5")
                              .content("boardContent5")
                              .user(user2)
                              .category(category)
                              .createdAt(LocalDateTime.now())
                              .imagePathList(imagePathList)
                              .filePathList(filePathList)
                              .build()
        );

        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaUserRepository.save(user2);
        jpaCategoryRepository.save(category);
        jpaCategoryRepository.save(category2);
        jpaBoardRepository.saveAll(boardList);

        entityManager.flush();
        entityManager.clear();
        return;
    }


}
