package org.waldreg.repository.category;

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
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCategoryStorage;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.repository.board.BoardMapper;
import org.waldreg.repository.board.MemoryBoardRepository;
import org.waldreg.repository.character.CharacterMapper;
import org.waldreg.repository.character.MemoryCharacterRepository;
import org.waldreg.repository.user.MemoryUserRepository;
import org.waldreg.repository.user.UserMapper;
import org.waldreg.user.spi.UserRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MemoryCategoryRepository.class, CategoryMapper.class, MemoryCategoryStorage.class, MemoryBoardRepository.class, MemoryBoardStorage.class, MemoryUserRepository.class, MemoryUserStorage.class, MemoryCharacterRepository.class, MemoryCharacterStorage.class, BoardMapper.class, UserMapper.class, CharacterMapper.class})
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
                () -> Assertions.assertEquals(categoryDto.getBoardDtoList(), result.get(0).getBoardDtoList()),
                () -> Assertions.assertEquals(categoryDto2.getCategoryName(), result.get(1).getCategoryName()),
                () -> Assertions.assertEquals(categoryDto2.getBoardDtoList(), result.get(1).getBoardDtoList()),
                () -> Assertions.assertEquals(categoryDto3.getCategoryName(), result.get(2).getCategoryName()),
                () -> Assertions.assertEquals(categoryDto3.getBoardDtoList(), result.get(2).getBoardDtoList())
        );

    }

    @Test
    @DisplayName("카테고리 삭제 성공 테스트")
    public void DELETE_CATEGORY_BY_ID_SUCCESS_TEST(){
        //given
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();

        //when
        categoryRepository.createCategory(categoryDto);
        CategoryDto categoryResponse = categoryRepository.inquiryAllCategory().get(0);
        categoryRepository.deleteCategory(categoryResponse.getId());

        //then
        Assertions.assertFalse(()->categoryRepository.isExistCategory(categoryResponse.getId()));
    }


}
