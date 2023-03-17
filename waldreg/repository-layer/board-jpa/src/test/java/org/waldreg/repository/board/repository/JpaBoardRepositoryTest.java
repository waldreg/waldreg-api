package org.waldreg.repository.board.repository;

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
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
class JpaBoardRepositoryTest{

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    JpaCommentRepository jpaCommentRepository;

    @Autowired
    private JpaBoardRepository jpaBoardRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    public void INIT_BOARD(){
        jpaCommentRepository.deleteAll();
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void CREATE_BOARD_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when
        Board foundBoard = jpaBoardRepository.findAll().get(0);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(board.getUser().getUserId(), foundBoard.getUser().getUserId()),
                () -> Assertions.assertEquals(board.getUser().getUserPassword(), foundBoard.getUser().getUserPassword()),
                () -> Assertions.assertEquals(board.getUser().getName(), foundBoard.getUser().getName()),
                () -> Assertions.assertEquals(board.getCategory().getCategoryName(), foundBoard.getCategory().getCategoryName()),
                () -> Assertions.assertEquals(board.getTitle(), foundBoard.getTitle()),
                () -> Assertions.assertEquals(board.getContent(), foundBoard.getContent()),
                () -> Assertions.assertEquals(board.getCreatedAt().withNano(0), foundBoard.getCreatedAt().withNano(0)),
                () -> Assertions.assertEquals(board.getImagePathList().get(0), foundBoard.getImagePathList().get(0)),
                () -> Assertions.assertEquals(board.getFilePathList().get(0), foundBoard.getFilePathList().get(0))
        );

    }

    @Test
    @DisplayName("아이디로 게시글 조회 성공 테스트")
    void INQUIRY_BOARD_BY_ID_SUCCESS_TEST(){

        //given
        Board board = setDefaultBoard();
        int boardId = board.getId();
        //when
        Board foundBoard = jpaBoardRepository.findById(boardId).get();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(board.getUser().getUserId(), foundBoard.getUser().getUserId()),
                () -> Assertions.assertEquals(board.getUser().getUserPassword(), foundBoard.getUser().getUserPassword()),
                () -> Assertions.assertEquals(board.getUser().getName(), foundBoard.getUser().getName()),
                () -> Assertions.assertEquals(board.getCategory().getCategoryName(), foundBoard.getCategory().getCategoryName()),
                () -> Assertions.assertEquals(board.getTitle(), foundBoard.getTitle()),
                () -> Assertions.assertEquals(board.getContent(), foundBoard.getContent()),
                () -> Assertions.assertEquals(board.getCreatedAt().withNano(0), foundBoard.getCreatedAt().withNano(0)),
                () -> Assertions.assertEquals(board.getImagePathList().get(0), foundBoard.getImagePathList().get(0)),
                () -> Assertions.assertEquals(board.getFilePathList().get(0), foundBoard.getFilePathList().get(0))
        );

    }

    @Test
    @DisplayName("게시글 삭제 성공 테스트")
    void DELETE_BOARD_BY_ID_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        String content = "comment content";

        Comment comment = Comment.builder()
                .board(board)
                .user(board.getUser())
                .content(content)
                .build();
        jpaCommentRepository.save(comment);
        Integer commentId = jpaCommentRepository.findAll().get(0).getId();
        entityManager.flush();
        entityManager.clear();
        //when
        jpaBoardRepository.deleteById(board.getId());
        entityManager.flush();
        entityManager.clear();
        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(jpaCommentRepository.existsById(commentId)),
                () -> Assertions.assertFalse(jpaBoardRepository.existsById(board.getId()))
        );
    }

    @Test
    @DisplayName("게시글 수정 성공 테스트")
    void MODIFY_BOARD_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when
        Category category = Category.builder()
                .categoryName("modifiedCategory")
                .build();
        jpaCategoryRepository.save(category);
        entityManager.flush();

        board.setContent("modifiedContent");
        board.setTitle("modifiedTitle");
        board.setCategory(category);
        board.setLastModifiedAt(LocalDateTime.now());
        jpaBoardRepository.save(board);
        entityManager.flush();
        entityManager.clear();

        Board foundBoard = jpaBoardRepository.findById(board.getId()).get();
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(board.getTitle(), foundBoard.getTitle()),
                () -> Assertions.assertEquals(board.getContent(), foundBoard.getContent()),
                () -> Assertions.assertEquals(board.getCategory().getCategoryName(), foundBoard.getCategory().getCategoryName()),
                () -> Assertions.assertEquals(board.getLastModifiedAt().withNano(0), foundBoard.getLastModifiedAt().withNano(0))
        );
    }

    @Test
    @DisplayName("게시글 존재 여부 확인 테스트")
    void IS_EXIST_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();

        //when
        //then
        Assertions.assertTrue(jpaBoardRepository.existsById(board.getId()));
    }

    @Test
    @DisplayName("전체 게시글 개수 테스트")
    void GET_MAX_INDEX_TEST(){
        setDefaultBoardList();

        Assertions.assertEquals(jpaBoardRepository.count(), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 제목")
    void GET_MAX_INDEX_BY_TITLE_TEST(){
        setDefaultBoardList();
        String title = "boardTitle";
        Assertions.assertEquals(jpaBoardRepository.getBoardMaxIdxByTitle(title), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 내용")
    void GET_MAX_INDEX_BY_CONTENT_TEST(){
        setDefaultBoardList();
        String content = "boardContent";
        Assertions.assertEquals(jpaBoardRepository.getBoardMaxIdxByContent(content), 5);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 유저 아이디")
    void GET_MAX_INDEX_BY_USER_ID_TEST(){
        setDefaultBoardList();
        String userId = "Fixtar";
        Assertions.assertEquals(jpaBoardRepository.getBoardMaxIdxByUserId(userId), 3);
    }

    @Test
    @DisplayName("게시글 개수 조회 테스트 - 카테고리 아이디")
    void GET_MAX_INDEX_BY_CATEGORY_ID_TEST(){
        setDefaultBoardList();

        Integer categoryId = jpaBoardRepository.findAll().get(0).getCategory().getId();

        Assertions.assertEquals(jpaBoardRepository.getBoardMaxIdxByCategoryId(categoryId), 3);
    }

    private Board setDefaultBoard(){
        Character character = Character.builder()
                .characterName("Guest")
                .permissionList(List.of())
                .build();

        User user = User.builder()
                .userId("Fixtar")
                .name("123")
                .userPassword("abcd")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();

        Category category = Category.builder()
                .categoryName("cate1")
                .build();

        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");
        Board board = Board.builder()
                .title("boardTitle")
                .content("boardContent")
                .user(user)
                .category(category)
                .createdAt(LocalDateTime.now())
                .imagePathList(imagePathList)
                .filePathList(filePathList)
                .build();

        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaCategoryRepository.save(category);
        jpaBoardRepository.save(board);

        entityManager.flush();
        entityManager.clear();
        return board;
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
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        User user2 = User.builder()
                .userId("secondUserId")
                .name("abce")
                .userPassword("abcd")
                .phoneNumber("010-1234-5678")
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
