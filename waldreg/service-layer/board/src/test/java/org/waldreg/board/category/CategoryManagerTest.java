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
import org.waldreg.board.category.exception.DuplicateCategoryNameException;
import org.waldreg.board.category.management.CategoryManager;
import org.waldreg.board.category.management.DefaultCategoryManager;
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.BoardServiceMemberTier;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultCategoryManager.class, DecryptedTokenContext.class})
public class CategoryManagerTest{

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
        BoardServiceMemberTier boardServiceMemberTier = BoardServiceMemberTier.TIER_3;
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .memberTier(boardServiceMemberTier)
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
        BoardServiceMemberTier boardServiceMemberTier = BoardServiceMemberTier.TIER_3;
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .memberTier(boardServiceMemberTier)
                .build();
        //when
        Mockito.when(categoryRepository.isDuplicateCategoryName(Mockito.anyString())).thenReturn(true);
        //then
        Assertions.assertThrows(DuplicateCategoryNameException.class, () -> categoryManager.createCategory(categoryDto));
    }

}
