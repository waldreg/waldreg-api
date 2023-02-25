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
public class JpaCommentRepositoryTest{

    @Autowired
    private TestJpaUserRepository testJpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private TestJpaCharacterRepository testJpaCharacterRepository;

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
        testJpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        testJpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void CREATE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = testJpaCharacterRepository.findAll().get(0);
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

        testJpaCharacterRepository.save(character);
        testJpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);

        Assertions.assertAll(
                ()->Assertions.assertEquals(comment.getBoard().getId(),foundComment.getBoard().getId()),
                ()->Assertions.assertEquals(comment.getUser().getUserId(),foundComment.getUser().getUserId()),
                ()->Assertions.assertEquals(comment.getUser().getUserPassword(),foundComment.getUser().getUserPassword()),
                ()->Assertions.assertEquals(comment.getContent(),foundComment.getContent()),
                ()->Assertions.assertEquals(comment.getCreatedAt(),foundComment.getCreatedAt())
        );


    }

    @Test
    @DisplayName("댓글 조회 테스트 - 게시글 아이디, from ,to")
    void INQUIRY_COMMENT_LIST_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = testJpaCharacterRepository.findAll().get(0);
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


        testJpaCharacterRepository.save(character);
        testJpaUserRepository.save(user);
        testJpaUserRepository.save(user2);
        jpaCommentRepository.save(comment);
        jpaCommentRepository.save(comment2);
        jpaCommentRepository.save(comment3);
        entityManager.flush();
        entityManager.clear();

        List<Comment> commentList = jpaCommentRepository.findAllByBoardId(board.getId(),0,5);

        Assertions.assertAll(
                ()-> Assertions.assertEquals(3, commentList.size()),
                ()->Assertions.assertEquals(comment.getContent(),commentList.get(0).getContent()),
                ()->Assertions.assertEquals(comment.getUser().getUserId(),commentList.get(0).getUser().getUserId()),
                ()->Assertions.assertEquals(comment2.getContent(),commentList.get(1).getContent()),
                ()->Assertions.assertEquals(comment2.getUser().getUserId(),commentList.get(1).getUser().getUserId()),
                ()->Assertions.assertEquals(comment3.getContent(),commentList.get(2).getContent()),
                ()->Assertions.assertEquals(comment3.getUser().getUserId(),commentList.get(2).getUser().getUserId())
                );

    }


    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void UPDATE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when
        Character character = testJpaCharacterRepository.findAll().get(0);
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
        testJpaCharacterRepository.save(character);
        testJpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);
        foundComment.setContent("modified");
        jpaCommentRepository.saveAndFlush(foundComment);
        entityManager.clear();
        Comment result = jpaCommentRepository.findAll().get(0);

        Assertions.assertAll(
                ()->Assertions.assertEquals(comment.getBoard().getId(),result.getBoard().getId()),
                ()->Assertions.assertEquals(comment.getUser().getUserId(),result.getUser().getUserId()),
                ()->Assertions.assertEquals(comment.getUser().getUserPassword(),result.getUser().getUserPassword()),
                ()->Assertions.assertEquals("modified",result.getContent()),
                ()->Assertions.assertEquals(comment.getCreatedAt(),result.getCreatedAt())
        );


    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void DELETE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        //when

        Character character = testJpaCharacterRepository.findAll().get(0);
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
        testJpaCharacterRepository.save(character);
        testJpaUserRepository.save(user);
        jpaCommentRepository.save(comment);
        entityManager.flush();
        entityManager.clear();

        Comment foundComment = jpaCommentRepository.findAll().get(0);
        jpaCommentRepository.deleteById(foundComment.getId());
        entityManager.flush();
        entityManager.clear();

        Assertions.assertFalse(jpaCommentRepository.existsById(foundComment.getId()));
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

        testJpaCharacterRepository.save(character);
        testJpaUserRepository.save(user);
        jpaCategoryRepository.save(category);
        jpaBoardRepository.save(board);

        entityManager.flush();
        entityManager.clear();
        return board;
    }


}
