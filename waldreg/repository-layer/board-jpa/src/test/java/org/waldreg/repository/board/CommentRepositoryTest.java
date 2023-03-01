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
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.CommentRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaCommentRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {
        CommentRepositoryServiceProvider.class,
        CommentRepositoryMapper.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class CommentRepositoryTest{

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private JpaCommentRepository jpaCommentRepository;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    private void INIT(){
        jpaCommentRepository.deleteAll();
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("새로운 댓글 생성 성공 테스트")
    public void CREATE_NEW_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto result = commentRepository.createComment(commentDto);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(commentDto.getBoardId(), result.getBoardId()),
                () -> Assertions.assertEquals(commentDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(commentDto.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(commentDto.getUserDto().getId(), result.getUserDto().getId())
        );

    }

    @Test
    @DisplayName("댓글 전체 조회 테스트")
    public void INQUIRY_ALL_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        User user2 = User.builder()
                .userId("commentUser2")
                .name("aaaa2")
                .userPassword("bocda2")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user2);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(user2.getId())
                .userId(user2.getUserId())
                .name(user2.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet2")
                .userDto(userDto2)
                .build();
        CommentDto commentDto3 = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet3")
                .userDto(userDto)
                .build();
        commentRepository.createComment(commentDto);
        commentRepository.createComment(commentDto2);
        commentRepository.createComment(commentDto3);
        //when
        List<CommentDto> result = commentRepository.inquiryAllCommentByBoardId(board.getId(), 0, 5);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(commentDto.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(commentDto.getBoardId(), result.get(0).getBoardId()),
                () -> Assertions.assertEquals(commentDto.getUserDto().getUserId(), result.get(0).getUserDto().getUserId()),
                () -> Assertions.assertEquals(commentDto2.getContent(), result.get(1).getContent()),
                () -> Assertions.assertEquals(commentDto2.getBoardId(), result.get(1).getBoardId()),
                () -> Assertions.assertEquals(commentDto2.getUserDto().getUserId(), result.get(1).getUserDto().getUserId()),
                () -> Assertions.assertEquals(commentDto3.getContent(), result.get(2).getContent()),
                () -> Assertions.assertEquals(commentDto3.getBoardId(), result.get(2).getBoardId()),
                () -> Assertions.assertEquals(commentDto3.getUserDto().getUserId(), result.get(2).getUserDto().getUserId())
        );

    }


    @Test
    @DisplayName("댓글 아이디로 댓글 하나 조회")
    public void INQUIRY_COMMENT_BY_ID_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        CommentDto result = commentRepository.inquiryCommentById(createdCommentDto.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(createdCommentDto.getBoardId(), result.getBoardId()),
                () -> Assertions.assertEquals(createdCommentDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(createdCommentDto.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(createdCommentDto.getUserDto().getId(), result.getUserDto().getId()),
                () -> Assertions.assertEquals(createdCommentDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(createdCommentDto.getLastModifiedAt(), result.getLastModifiedAt())
        );
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    public void MODIFY_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        createdCommentDto.setContent("modified");

        commentRepository.modifyComment(createdCommentDto);
        CommentDto result = commentRepository.inquiryCommentById(createdCommentDto.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(createdCommentDto.getId(), result.getId()),
                () -> Assertions.assertEquals(createdCommentDto.getCreatedAt(), result.getCreatedAt()),
                () -> Assertions.assertEquals(createdCommentDto.getBoardId(), result.getBoardId()),
                () -> Assertions.assertEquals(createdCommentDto.getUserDto().getUserId(), result.getUserDto().getUserId()),
                () -> Assertions.assertEquals(createdCommentDto.getContent(), result.getContent()),
                () -> Assertions.assertNotEquals(createdCommentDto.getLastModifiedAt(), result.getLastModifiedAt())
        );

    }

    @Test
    @DisplayName("댓글이 존재 하는지 테스트")
    public void IS_EXIST_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        //then
        Assertions.assertTrue(commentRepository.isExistComment(createdCommentDto.getId()));
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    public void DELETE_COMMENT_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        commentRepository.deleteComment(createdCommentDto.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(commentRepository.isExistComment(createdCommentDto.getId())),
                () -> Assertions.assertTrue(jpaUserRepository.existsById(user.getId())),
                () -> Assertions.assertTrue(jpaBoardRepository.existsById(board.getId()))
        );
    }

    @Test
    @DisplayName("보드 삭제시 댓글 삭제 성공 테스트")
    public void DELETE_COMMENT_BY_DELETE_BOARD_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        jpaBoardRepository.deleteById(board.getId());
        entityManager.flush();
        entityManager.clear();
        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(commentRepository.isExistComment(createdCommentDto.getId())),
                () -> Assertions.assertTrue(jpaUserRepository.existsById(user.getId())),
                () -> Assertions.assertFalse(jpaBoardRepository.existsById(board.getId()))
        );
    }


    @Test
    @DisplayName("게시글 하나에 댓글이 몇개 있는지 테스트")
    public void GET_COMMENT_MAX_IDX_BY_BOARD_ID(){
        //given
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        User user2 = User.builder()
                .userId("commentUser2")
                .name("aaaa2")
                .userPassword("bocda2")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user2);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(user2.getId())
                .userId(user2.getUserId())
                .name(user2.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet2")
                .userDto(userDto2)
                .build();
        CommentDto commentDto3 = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet3")
                .userDto(userDto)
                .build();
        commentRepository.createComment(commentDto);
        commentRepository.createComment(commentDto2);
        commentRepository.createComment(commentDto3);
        //when
        int result = commentRepository.getCommentMaxIdxByBoardId(board.getId());
        //then
        Assertions.assertEquals(3, result);
    }


    @Test
    @DisplayName("comment 생성시 board 에서 접근 가능한지 테스트")
    void COMMENT_BOARD_ACCESS_TEST(){
        Board board = setDefaultBoard();
        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("commentUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(board.getId())
                .content("commnet1")
                .userDto(userDto)
                .build();
        //when
        CommentDto createdCommentDto = commentRepository.createComment(commentDto);
        entityManager.flush();
        entityManager.clear();

        Board board1 = jpaBoardRepository.findById(board.getId()).get();

        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(board1.getCommentList().size(),1),
                ()->Assertions.assertEquals(board1.getCommentList().get(0).getContent(),commentDto.getContent()),
                ()->Assertions.assertEquals(board1.getCommentList().get(0).getUser().getUserId(),commentDto.getUserDto().getUserId())

        );


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
