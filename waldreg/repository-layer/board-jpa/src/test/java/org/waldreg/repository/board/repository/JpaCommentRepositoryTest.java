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
import org.waldreg.repository.board.CommentCommander;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaCommentRepositoryTest{

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private JpaCommentRepository jpaCommentRepository;

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
    @DisplayName("댓글 생성 성공 테스트")
    void CREATE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();

        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();

        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(comment.getBoard().getId(), foundComment.getBoard().getId()),
                () -> Assertions.assertEquals(comment.getUser().getUserId(), foundComment.getUser().getUserId()),
                () -> Assertions.assertEquals(comment.getUser().getUserPassword(), foundComment.getUser().getUserPassword()),
                () -> Assertions.assertEquals(comment.getContent(), foundComment.getContent()),
                () -> Assertions.assertEquals(comment.getCreatedAt().withNano(0), foundComment.getCreatedAt().withNano(0))
        );


    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void UPDATE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();

        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);
        foundComment.setContent("modified");
        jpaCommentRepository.saveAndFlush(foundComment);
        entityManager.clear();
        Comment result = jpaCommentRepository.findAll().get(0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(comment.getBoard().getId(), result.getBoard().getId()),
                () -> Assertions.assertEquals(comment.getUser().getUserId(), result.getUser().getUserId()),
                () -> Assertions.assertEquals(comment.getUser().getUserPassword(), result.getUser().getUserPassword()),
                () -> Assertions.assertEquals("modified", result.getContent()),
                () -> Assertions.assertEquals(comment.getCreatedAt().withNano(0), result.getCreatedAt().withNano(0))
        );


    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void DELETE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();

        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);
        jpaCommentRepository.deleteById(foundComment.getId());
        entityManager.flush();
        entityManager.clear();

        Assertions.assertFalse(jpaCommentRepository.existsById(foundComment.getId()));
    }

    @Test
    @DisplayName("게시글의 댓글 개수 조회")
    void GET_COMMENT_MAX_IDX_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        User user2 = User.builder()
                .userId("commentUser2")
                .name("aaaa2")
                .userPassword("bocda2")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();

        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .content("comment2")
                .user(user2)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
        Comment comment3 = Comment.builder()
                .content("comment3")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();

        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaUserRepository.save(user2);
        jpaCommentRepository.save(comment);
        jpaCommentRepository.save(comment2);
        jpaCommentRepository.save(comment3);
        entityManager.flush();
        entityManager.clear();
        //when
        int result = jpaCommentRepository.getBoardMaxIdxByBoardId(board.getId());
        //then
        Assertions.assertEquals(3, result);
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


}
