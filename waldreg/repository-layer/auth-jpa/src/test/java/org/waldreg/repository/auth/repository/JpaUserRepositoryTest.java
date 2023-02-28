package org.waldreg.repository.auth.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
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
public class JpaUserRepositoryTest{

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaCommentRepository jpaCommentRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("유저 아이디, 비밀번호로 유저 조회 성공")
    public void READ_USER_BY_USER_ID_AND_USER_PASSWORD_TEST(){
        //given
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
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        //when
        User foundUser = jpaUserRepository.findUserByUserId("Fixtar").get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword()),
                () -> Assertions.assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber()),
                () -> Assertions.assertEquals(user.getCharacter().getCharacterName(), foundUser.getCharacter().getCharacterName())
        );
    }

    @Test
    @DisplayName("id로 유저 조회 테스트")
    public void READ_USER_BY_ID_TEST(){
        //given
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
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        //when
        User foundUser = jpaUserRepository.findById(user.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword()),
                () -> Assertions.assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber()),
                () -> Assertions.assertEquals(user.getCharacter().getCharacterName(), foundUser.getCharacter().getCharacterName())
        );

    }

    @Test
    @DisplayName("게시글 id로 유저 조회 테스트")
    public void READ_USER_BY_BOARD_ID_TEST(){
        //given
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
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);

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
        jpaCategoryRepository.save(category);
        jpaBoardRepository.save(board);

        entityManager.flush();
        entityManager.clear();

        //when
        User foundUser = jpaUserRepository.findUserByBoardId(board.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword()),
                () -> Assertions.assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber()),
                () -> Assertions.assertEquals(user.getCharacter().getCharacterName(), foundUser.getCharacter().getCharacterName())
        );

    }

    @Test
    @DisplayName("댓글 id로 유저 조회 테스트")
    public void READ_USER_BY_COMMENT_ID_TEST(){
        //given
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
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);

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
        jpaCategoryRepository.save(category);
        jpaBoardRepository.save(board);
        Comment comment = Comment.builder()
                .content("comment1")
                .user(user)
                .board(board)
                .createdAt(LocalDateTime.now())
                .build();
        jpaCommentRepository.save(comment);

        entityManager.flush();
        entityManager.clear();

        //when
        User foundUser = jpaUserRepository.findUserByCommentId(comment.getId()).get();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), foundUser.getUserId()),
                () -> Assertions.assertEquals(user.getId(), foundUser.getId()),
                () -> Assertions.assertEquals(user.getUserPassword(), foundUser.getUserPassword()),
                () -> Assertions.assertEquals(user.getPhoneNumber(), foundUser.getPhoneNumber()),
                () -> Assertions.assertEquals(user.getCharacter().getCharacterName(), foundUser.getCharacter().getCharacterName())
        );

    }


    }
