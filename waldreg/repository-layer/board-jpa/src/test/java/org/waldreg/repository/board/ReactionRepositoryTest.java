package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.board.reaction.spi.ReactionUserRepository;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.ReactionRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaReactionRepository;
import org.waldreg.repository.board.repository.JpaReactionUserRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {
        ReactionRepositoryServiceProvider.class,
        ReactionRepositoryMapper.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class ReactionRepositoryTest{

    @Autowired
    private ReactionInBoardRepository reactionInBoardRepository;
    @Autowired
    private ReactionUserRepository reactionUserRepository;
    @Autowired
    private JpaReactionRepository jpaReactionRepository;
    @Autowired
    private JpaReactionUserRepository jpaReactionUserRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    private void INIT(){
        jpaReactionUserRepository.deleteAll();
        jpaReactionRepository.deleteAll();
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("빈 reaction 6개 생성 성공 테스트")
    public void CREATE_REACTION_SUCCESS_TEST(){
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

        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());

        ReactionDto reactionDto = ReactionDto.builder()
                .boardId(board.getId())
                .reactionMap(reactionMap)
                .build();
        //then
        Assertions.assertDoesNotThrow(() -> reactionInBoardRepository.storeReactionDto(reactionDto));

    }

    @Test
    @DisplayName("reactionUser 추가 성공 테스트")
    public void ADD_REACTION_SUCCESS_TEST(){
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

        //when
        ReactionDto reactionDto1 = reactionInBoardRepository.getReactionDto(board.getId());
        Map<BoardServiceReactionType, List<UserDto>> reactionMap1 = reactionDto1.getReactionMap();
        List<UserDto> userDtoList = reactionMap1.get(BoardServiceReactionType.GOOD);
        userDtoList.add(UserDto.builder().id(user.getId())
                                .userId(user.getUserId())
                                .name(user.getName())
                                .build());
        reactionMap1.put(BoardServiceReactionType.GOOD, userDtoList);
        reactionDto1.setReactionMap(reactionMap1);
        reactionInBoardRepository.storeReactionDto(reactionDto1);
        entityManager.flush();
        entityManager.clear();

        ReactionDto result = reactionInBoardRepository.getReactionDto(board.getId());
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(userDtoList.size(), result.getReactionMap().get(BoardServiceReactionType.GOOD).size()),
                () -> Assertions.assertEquals(userDtoList.get(0).getUserId(), result.getReactionMap().get(BoardServiceReactionType.GOOD).get(0).getUserId()),
                () -> Assertions.assertEquals(userDtoList.get(0).getName(), result.getReactionMap().get(BoardServiceReactionType.GOOD).get(0).getName()),
                () -> Assertions.assertEquals(reactionMap1.size(), result.getReactionMap().size()),
                () -> Assertions.assertEquals(reactionMap1.get(BoardServiceReactionType.BAD).size(), result.getReactionMap().get(BoardServiceReactionType.BAD).size()),
                () -> Assertions.assertEquals(reactionMap1.get(BoardServiceReactionType.CHECK).size(), result.getReactionMap().get(BoardServiceReactionType.CHECK).size()),
                () -> Assertions.assertEquals(reactionMap1.get(BoardServiceReactionType.SAD).size(), result.getReactionMap().get(BoardServiceReactionType.SAD).size()),
                () -> Assertions.assertEquals(reactionMap1.get(BoardServiceReactionType.HEART).size(), result.getReactionMap().get(BoardServiceReactionType.HEART).size()),
                () -> Assertions.assertEquals(reactionMap1.get(BoardServiceReactionType.SMILE).size(), result.getReactionMap().get(BoardServiceReactionType.SMILE).size())

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

        List<Reaction> reactionList = new ArrayList<>();
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.HEART.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.SAD.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.GOOD.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.BAD.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.CHECK.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );
        reactionList.add(Reaction.builder()
                                 .type(BoardServiceReactionType.SMILE.name())
                                 .reactionUserList(new ArrayList<>())
                                 .board(board)
                                 .build()
        );

        jpaReactionRepository.saveAll(reactionList);
        entityManager.flush();
        entityManager.clear();
        return board;
    }


}
