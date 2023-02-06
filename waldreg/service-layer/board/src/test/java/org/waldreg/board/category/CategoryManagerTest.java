package org.waldreg.board.category;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.spi.UserRepository;
import org.waldreg.board.category.exception.CategoryDoesNotExistException;
import org.waldreg.board.category.exception.DuplicateCategoryNameException;
import org.waldreg.board.category.management.CategoryManager;
import org.waldreg.board.category.management.DefaultCategoryManager;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultCategoryManager.class, DecryptedTokenContext.class})
public class CategoryManagerTest{

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private CategoryManager categoryManager;

    @Autowired
    private DecryptedTokenContext decryptedTokenContext;

    @MockBean
    private CategoryRepository categoryRepository;

    @BeforeEach
    @AfterEach
    public void INIT_USER_TOKEN(){
        decryptedTokenContext.resolve();
        decryptedTokenContext.hold(1);
    }

    @Test
    @DisplayName("카테고리 생성 성공 테스트")
    public void CREATE_CATEGORY_SUCCESS_TEST(){
        //given
        String categoryName = "title";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        //when
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(false);
        //then
        Assertions.assertDoesNotThrow(() -> categoryManager.createCategory(categoryDto));
    }

    @Test
    @DisplayName("카테고리 생성 실패 중복된 카테고리")
    public void CREATE_CATEGORY_DUPLICATE_CATEGORY_NAME_TEST(){
        //given
        String categoryName = "title";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        //when
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicateCategoryNameException.class, () -> categoryManager.createCategory(categoryDto));
    }

    @Test
    @DisplayName("전체 카테고리 조회")
    public void INQUIRY_ALL_CATEGORY_TEST(){
        Assertions.assertDoesNotThrow(() -> categoryManager.inquiryAllCategory());
    }

    @Test
    @DisplayName("카테고리 수정 성공 테스트")
    public void MODIFY_CATEGORY_SUCCESS_TEST(){
        //given
        int categoryId = 1;
        String categoryName = "title";
        CategoryDto categoryDto = CategoryDto.builder()
                .id(categoryId)
                .categoryName(categoryName)
                .build();

        String modifyCategoryName = "modify title";
        CategoryDto modifyCategoryDto = CategoryDto.builder()
                .id(categoryDto.getId())
                .categoryName(modifyCategoryName)
                .build();

        //when
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(false);
        Mockito.when(categoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);

        //then
        Assertions.assertDoesNotThrow(() -> categoryManager.modifyCategory(modifyCategoryDto));
    }

    @Test
    @DisplayName("카테고리 수정 실패 테스트 - 존재하지 않는 카테고리 아이디")
    public void MODIFY_CATEGORY_DOES_NOT_EXIST_CATEGORY_ID_TEST(){
        //given
        int categoryId = 1;
        String categoryName = "title";
        CategoryDto categoryDto = CategoryDto.builder()
                .id(categoryId)
                .categoryName(categoryName)
                .build();

        String modifyCategoryName = "modify title";
        CategoryDto modifyCategoryDto = CategoryDto.builder()
                .id(categoryDto.getId())
                .categoryName(modifyCategoryName)
                .build();

        //when
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(false);
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(false);
        Mockito.when(categoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);

        //then
        Assertions.assertThrows(CategoryDoesNotExistException.class, () -> categoryManager.modifyCategory(modifyCategoryDto));
    }

    @Test
    @DisplayName("카테고리 수정 실패 테스트 - 중복된 카테고리 이름")
    public void MODIFY_CATEGORY_DUPLICATE_CATEGORY_NAME_TEST(){
        //given
        int categoryId = 1;
        String categoryName = "title";
        CategoryDto categoryDto = CategoryDto.builder()
                .id(categoryId)
                .categoryName(categoryName)
                .build();

        String modifyCategoryName = "modify title";
        CategoryDto modifyCategoryDto = CategoryDto.builder()
                .id(categoryDto.getId())
                .categoryName(modifyCategoryName)
                .build();

        //when
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(true);
        Mockito.when(categoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);

        //then
        Assertions.assertThrows(DuplicateCategoryNameException.class, () -> categoryManager.modifyCategory(modifyCategoryDto));
    }

    @Test
    @DisplayName("카테고리 삭제 성공 테스트")
    public void DELETE_CATEGORY_SUCCESS_TEST(){
        //given
        int categoryId = 1;
        //when
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        //then
        Assertions.assertDoesNotThrow(() -> categoryManager.deleteCategory(categoryId));

    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 없는 카테고리 아이디")
    public void DELETE_CATEGORY_DOES_NOT_EXIST_ID_TEST(){
        //given
        int categoryId = 1;
        //when
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(false);
        //then
        Assertions.assertThrows(CategoryDoesNotExistException.class,() -> categoryManager.deleteCategory(categoryId));
    }
}
