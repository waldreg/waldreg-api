package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.BoardRepositoryMapper;
import org.waldreg.repository.board.mapper.CategoryRepositoryMapper;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {BoardRepositoryServiceProvider.class, CategoryRepositoryServiceProvider.class,
        BoardRepositoryMapper.class, CategoryRepositoryMapper.class,
        JpaCharacterRepository.class, JpaCategoryRepository.class, JpaUserRepository.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
class BoardRepositoryTest{

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private BoardRepositoryMapper boardRepositoryMapper;

    @BeforeEach
    @AfterEach
    private void INIT(){
        jpaCategoryRepository.deleteAll();
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
                .phoneNumber("010-1234-5678")
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
                () -> Assertions.assertEquals(boardDto.getFileUrls(), result.getFileUrls())
        );
    }

    @Test
    @DisplayName("아이디로 게시글 조회 성공 테스트")
    void INQUIRY_BOARD_BY_ID_TEST(){
        //given
        BoardDto boardDto = createDefaultBoard();
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

    private BoardDto createDefaultBoard(){
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
                .phoneNumber("010-1234-5678")
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


}
