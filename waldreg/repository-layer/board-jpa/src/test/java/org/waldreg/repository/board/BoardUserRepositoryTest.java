package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.board.board.spi.BoardUserRepository;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.BoardUserRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {BoardUserServiceProvider.class,
        JpaCharacterRepository.class, JpaCategoryRepository.class, JpaUserRepository.class, JpaBoardRepository.class,
        BoardUserRepositoryMapper.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class BoardUserRepositoryTest{

    @Autowired
    private BoardUserRepository boardUserRepository;
    @Autowired
    private BoardUserRepositoryMapper boardUserRepositoryMapper;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("id로 유저 정보 가져오기 성공 테스트")
    void GET_USER_INFO_BY_USER_ID_TEST(){
        //given
        Board board = setDefaultBoard();
        //when
        User user = board.getUser();
        UserDto userDto = boardUserRepository.getUserInfo(user.getId());

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getUserId(), userDto.getUserId()),
                () -> Assertions.assertEquals(user.getName(), userDto.getName()),
                () -> Assertions.assertEquals(user.getId(), userDto.getId())
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
