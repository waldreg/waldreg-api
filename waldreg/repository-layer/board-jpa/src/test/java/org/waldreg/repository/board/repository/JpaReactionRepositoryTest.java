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
import org.springframework.util.Assert;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.board.file.FileName;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.board.reaction.ReactionUser;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaReactionRepositoryTest{

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;

    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    JpaReactionRepository jpaReactionRepository;

    @Autowired
    JpaReactionUserRepository jpaReactionUserRepository;

    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaFileNameRepository jpaFileNameRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    public void INIT_BOARD(){
        jpaFileNameRepository.deleteAll();
        jpaReactionUserRepository.deleteAll();
        jpaReactionRepository.deleteAll();
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("리액션 생성 성공")
    void CREATE_REACTION_SUCCESS_TEST(){
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

        ReactionUser reactionUser = ReactionUser.builder()
                .user(user)
                .build();

        Reaction reaction = Reaction.builder()
                .board(board)
                .type("GOOD")
                .reactionUserList(List.of())
                .build();

        Assertions.assertDoesNotThrow(() -> jpaReactionRepository.save(reaction));
    }

    @Test
    @DisplayName("리액션 수정 성공")
    void MODIFY_REACTION_SUCCESS_TEST(){
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

        Reaction reaction = Reaction.builder()
                .board(board)
                .type("GOOD")
                .reactionUserList(List.of())
                .build();

        jpaUserRepository.save(user);
        jpaReactionRepository.save(reaction);
        entityManager.flush();
        entityManager.clear();

        Reaction foundReaction = jpaReactionRepository.findAll().get(0);
        ReactionUser reactionUser = ReactionUser.builder()
                .user(user)
                .reaction(foundReaction)
                .build();
        jpaReactionUserRepository.save(reactionUser);

        List<ReactionUser> reactionUserList = foundReaction.getReactionUserList();
        reactionUserList.add(reactionUser);
        foundReaction.setReactionUserList(reactionUserList);
        jpaReactionRepository.save(foundReaction);

        entityManager.flush();
        entityManager.clear();

        Reaction result = jpaReactionRepository.findById(foundReaction.getId()).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getType(), reaction.getType()),
                () -> Assertions.assertEquals(result.getBoard().getId(), reaction.getBoard().getId()),
                () -> Assertions.assertEquals(result.getReactionUserList().size(), 1),
                () -> Assertions.assertEquals(result.getId(), reaction.getId())
        );
    }


    @Test
    @DisplayName("리액션 조회 - boardId로 조회")
    public void INQUIRY_REACTION_LIST_BY_BOARD_ID_TEST(){
        //given
        Board board = setDefaultBoard();

        Character character = jpaCharacterRepository.findAll().get(0);
        User user = User.builder()
                .userId("reactionUser")
                .name("aaaa")
                .userPassword("bocda")
                .phoneNumber("010-1234-5678")
                .character(character)
                .build();
        jpaUserRepository.save(user);

        Board board2 = Board.builder()
                .title("22")
                .content("22222")
                .user(user)
                .category(board.getCategory())
                .build();
        jpaBoardRepository.save(board2);

        Reaction reaction = Reaction.builder()
                .board(board)
                .type("GOOD")
                .reactionUserList(List.of())
                .build();
        Reaction reaction2 = Reaction.builder()
                .board(board)
                .type("BAD")
                .reactionUserList(List.of())
                .build();
        Reaction reaction3 = Reaction.builder()
                .board(board2)
                .type("GOOD")
                .reactionUserList(List.of())
                .build();

        jpaReactionRepository.save(reaction);
        jpaReactionRepository.save(reaction2);
        jpaReactionRepository.save(reaction3);
        entityManager.flush();
        entityManager.clear();
        //when
        List<Reaction> reactionList = jpaReactionRepository.findByBoardId(board.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, reactionList.size()),
                () -> Assertions.assertEquals(reaction.getBoard().getId(), reactionList.get(0).getBoard().getId()),
                () -> Assertions.assertEquals(reaction.getBoard().getId(), reactionList.get(0).getBoard().getId())
        );


    }

    @Test
    @DisplayName("reaction 삭제 성공 - reaction id")
    public void DELETE_REACTION_BY_REACTION_ID_SUCCESS_TEST(){
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

        Reaction reaction = Reaction.builder()
                .board(board)
                .type("GOOD")
                .reactionUserList(List.of())
                .build();
        jpaUserRepository.save(user);
        jpaReactionRepository.save(reaction);
        entityManager.flush();
        entityManager.clear();

        Reaction foundReaction = jpaReactionRepository.findAll().get(0);
        ReactionUser reactionUser = ReactionUser.builder()
                .user(user)
                .reaction(foundReaction)
                .build();
        jpaReactionUserRepository.save(reactionUser);

        jpaReactionRepository.deleteById(reaction.getId());
        entityManager.flush();
        entityManager.clear();

        Assertions.assertAll(
                () -> Assertions.assertFalse(jpaReactionRepository.existsById(reaction.getId())),
                () -> Assertions.assertTrue(jpaUserRepository.existsById(user.getId())),
                () -> Assertions.assertFalse(jpaReactionUserRepository.existsById(reactionUser.getId()))
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

        FileName fileName = FileName.builder().origin("uuid.pptx").uuid("abasdf-adfa.pptx").build();
        FileName imageName = FileName.builder().origin("uuid.png").uuid("abasdf-adfa.png").build();

        List<FileName> filePathList = new ArrayList<>();
        filePathList.add(fileName);
        List<FileName> imagePathList = new ArrayList<>();
        filePathList.add(imageName);
        Board board = Board.builder()
                .title("boardTitle")
                .content("boardContent")
                .user(user)
                .category(category)
                .createdAt(LocalDateTime.now())
                .imagePathList(imagePathList)
                .filePathList(filePathList)
                .build();
        jpaFileNameRepository.save(fileName);
        jpaFileNameRepository.save(imageName);
        jpaCharacterRepository.save(character);
        jpaUserRepository.save(user);
        jpaCategoryRepository.save(category);
        jpaBoardRepository.save(board);

        entityManager.flush();
        entityManager.clear();
        return board;
    }


}
