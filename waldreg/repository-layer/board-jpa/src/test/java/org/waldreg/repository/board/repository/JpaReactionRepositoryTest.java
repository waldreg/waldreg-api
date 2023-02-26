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
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    public void INIT_BOARD(){
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

        Assertions.assertDoesNotThrow(()->jpaReactionRepository.save(reaction));
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
                ()->Assertions.assertEquals(result.getType(), reaction.getType()),
                ()->Assertions.assertEquals(result.getBoard().getId(), reaction.getBoard().getId()),
                ()->Assertions.assertEquals(result.getReactionUserList().size(),1),
                ()->Assertions.assertEquals(result.getId(), reaction.getId())
        );
    }

    @Test
    @DisplayName("리액션 취소 성공")
    void DELETE_REACTION_SUCCESS_TEST(){
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

        Reaction reaction1 = jpaReactionRepository.findById(foundReaction.getId()).get();
        ReactionUser deleteReactionUser = jpaReactionUserRepository.findAll().get(0);

        List<ReactionUser> resultUserList = reaction1.getReactionUserList();
        resultUserList.remove(deleteReactionUser);
        reaction1.setReactionUserList(resultUserList);
        jpaReactionRepository.save(reaction1);

        Reaction result = jpaReactionRepository.findById(foundReaction.getId()).get();
        Assertions.assertAll(
                ()->Assertions.assertEquals(result.getType(), reaction.getType()),
                ()->Assertions.assertEquals(result.getBoard().getId(), reaction.getBoard().getId()),
                ()->Assertions.assertEquals(result.getReactionUserList().size(),0),
                ()->Assertions.assertEquals(result.getId(), reaction.getId())
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
