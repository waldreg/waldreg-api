package org.waldreg.repository.category;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryCommentStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.board.BoardMapper;
import org.waldreg.repository.board.MemoryBoardRepository;
import org.waldreg.repository.boarduserinfo.UserInfoMapper;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.comment.CommentMapper;
import org.waldreg.repository.comment.MemoryCommentRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserInfoMapper.class, MemoryCommentStorage.class, MemoryCommentRepository.class, CommentMapper.class, MemoryCategoryRepository.class, CategoryMapper.class, MemoryCategoryStorage.class, MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class})
public class CategoryRepositoryTest{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemoryCategoryStorage memoryCategoryStorage;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemoryBoardStorage memoryBoardStorage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemoryUserStorage memoryUserStorage;

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private MemoryCharacterStorage memoryCharacterStorage;

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_CATEGORY(){memoryCategoryStorage.deleteAllCategory();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_CHARACTER(){memoryCharacterStorage.deleteAllCharacter();}

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_USER(){
        memoryUserStorage.deleteAllUser();
    }

    @BeforeEach
    @AfterEach
    private void DELETE_ALL_BOARD(){memoryBoardStorage.deleteAllBoard();}

    @Test
    @DisplayName("카테고리 생성 성공 테스트")
    public void CREATE_NEW_CATEGORY_SUCCESS_TEST(){
        //given
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> categoryRepository.createCategory(categoryDto));

    }

    @Test
    @DisplayName("전체 카테고리 조회 성공 테스트")
    public void INQUIRY_ALL_CATEGORY_TEST(){
        //given
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        String categoryName2 = "catecatecate";
        CategoryDto categoryDto2 = CategoryDto.builder()
                .categoryName(categoryName2)
                .build();
        String categoryName3 = "catecatecatecate";
        CategoryDto categoryDto3 = CategoryDto.builder()
                .categoryName(categoryName3)
                .build();

        //when
        categoryRepository.createCategory(categoryDto);
        categoryRepository.createCategory(categoryDto2);
        categoryRepository.createCategory(categoryDto3);
        List<CategoryDto> result = categoryRepository.inquiryAllCategory();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, result.size()),
                () -> Assertions.assertEquals(categoryDto.getCategoryName(), result.get(0).getCategoryName()),
                () -> Assertions.assertEquals(new ArrayList<>(), result.get(0).getBoardDtoList()),
                () -> Assertions.assertEquals(categoryDto2.getCategoryName(), result.get(1).getCategoryName()),
                () -> Assertions.assertEquals(new ArrayList<>(), result.get(1).getBoardDtoList()),
                () -> Assertions.assertEquals(categoryDto3.getCategoryName(), result.get(2).getCategoryName()),
                () -> Assertions.assertEquals(new ArrayList<>(), result.get(0).getBoardDtoList())
        );

    }

    @Test
    @DisplayName("카테고리 삭제 성공 테스트")
    public void DELETE_CATEGORY_BY_ID_SUCCESS_TEST(){
        //given
        org.waldreg.user.dto.UserDto user = org.waldreg.user.dto.UserDto.builder()
                .userId("alcuk_id")
                .name("alcuk")
                .userPassword("alcuk123!")
                .phoneNumber("010-1234-1234")
                .build();
        CharacterDto characterDto = CharacterDto.builder()
                .id(1)
                .characterName("Guest")
                .permissionDtoList(List.of())
                .build();
        characterRepository.createCharacter(characterDto);
        userRepository.createUser(user);
        org.waldreg.user.dto.UserDto userResponse = userRepository.readUserByUserId("alcuk_id");
        String title = "title";
        String content = "content";
        UserDto userDto = UserDto.builder()
                .id(userResponse.getId())
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("cate")
                .build();
        List<String> filePathList = new ArrayList<>();
        filePathList.add("uuid.pptx");
        List<String> imagePathList = new ArrayList<>();
        imagePathList.add("uuid.png");

        //when
        categoryRepository.createCategory(categoryDto);
        int categoryId = categoryRepository.inquiryAllCategory().get(0).getId();
        BoardDto boardRequest = BoardDto.builder()
                .title(title)
                .userDto(userDto)
                .content(content)
                .categoryId(categoryId)
                .lastModifiedAt(LocalDateTime.now())
                .fileUrls(filePathList)
                .imageUrls(imagePathList)
                .build();
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        categoryRepository.deleteCategory(categoryId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertFalse(() -> categoryRepository.isExistCategory(categoryId)),
                () -> Assertions.assertFalse(() -> boardRepository.isExistBoard(boardDto.getId()))
        );
    }

    @Test
    @DisplayName("특정 카테고리 조회 성공 테스트")
    public void INQUIRY_CATEGORY_BY_ID_SUCCESS_TEST(){
        //given
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        String categoryName2 = "catecatecate";
        CategoryDto categoryDto2 = CategoryDto.builder()
                .categoryName(categoryName2)
                .build();
        String categoryName3 = "catecatecatecate";
        CategoryDto categoryDto3 = CategoryDto.builder()
                .categoryName(categoryName3)
                .build();

        //when
        categoryRepository.createCategory(categoryDto);
        categoryRepository.createCategory(categoryDto2);
        categoryRepository.createCategory(categoryDto3);
        int categoryDtoId = categoryRepository.inquiryAllCategory().get(0).getId();
        CategoryDto result = categoryRepository.inquiryCategoryById(categoryDtoId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(categoryDtoId, result.getId()),
                () -> Assertions.assertEquals(categoryDto.getCategoryName(), result.getCategoryName()),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getBoardDtoList())
        );
    }

    @Test
    @DisplayName("카테고리 Board List에 Board 추가 성공 테스트")
    public void ADD_BOARD_IN_CATEGORY_BOARD_LIST_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("alcuk_id")
                .name("alcuk")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("catecate")
                .build();

        //when
        categoryRepository.createCategory(categoryDto);
        int categoryDtoId = categoryRepository.inquiryAllCategory().get(0).getId();
        BoardDto boardRequest = BoardDto.builder()
                .title("bobo")
                .userDto(userDto)
                .content("contcont")
                .categoryId(categoryDtoId)
                .fileUrls(List.of())
                .imageUrls(List.of())
                .build();
        BoardDto boardDto = boardRepository.createBoard(boardRequest);
        categoryRepository.addBoardInCategoryBoardList(boardDto);
        CategoryDto result = categoryRepository.inquiryCategoryById(categoryDtoId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(categoryDtoId, result.getId()),
                () -> Assertions.assertEquals(1, result.getBoardDtoList().size()),
                () -> Assertions.assertEquals(boardDto.getId(), result.getBoardDtoList().get(0).getId()),
                () -> Assertions.assertEquals(categoryDto.getCategoryName(), result.getCategoryName())
        );

    }

    @Test
    @DisplayName("특정 카테고리 수정 성공 테스트")
    public void MODIFY_CATEGORY_BY_ID_SUCCESS_TEST(){
        //given
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();

        //when
        categoryRepository.createCategory(categoryDto);
        int categoryDtoId = categoryRepository.inquiryAllCategory().get(0).getId();
        String categoryName2 = "catecatecate";
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(categoryDtoId)
                .categoryName(categoryName2)
                .build();
        categoryRepository.modifyCategory(categoryDto2);
        CategoryDto result = categoryRepository.inquiryCategoryById(categoryDtoId);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(categoryDtoId, result.getId()),
                () -> Assertions.assertEquals(categoryDto2.getCategoryName(), result.getCategoryName()),
                () -> Assertions.assertEquals(new ArrayList<>(), result.getBoardDtoList())
        );
    }


}
