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
import org.waldreg.board.category.spi.CategoryRepository;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.category.Category;
import org.waldreg.domain.board.file.FileName;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.CategoryRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCategoryRepository;
import org.waldreg.repository.board.repository.JpaCharacterRepository;
import org.waldreg.repository.board.repository.JpaFileNameRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@DataJpaTest
@ContextConfiguration(classes = {
        CategoryRepositoryServiceProvider.class,
        CategoryRepositoryMapper.class, BoardCommander.class,
        JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class CategoryRepositoryTest{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private JpaBoardRepository jpaBoardRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;
    @Autowired
    private JpaFileNameRepository jpaFileNameRepository;
    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    private void INIT(){
        jpaFileNameRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
    }

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
    @DisplayName("카테고리 조회 성공 테스트")
    void INQUIRY_CATEGORY_BY_ID_TEST(){
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        categoryRepository.createCategory(categoryDto);
        Integer categoryId = categoryRepository.inquiryAllCategory().get(0).getId();
        CategoryDto categoryDtoResult = categoryRepository.inquiryCategoryById(categoryId);

        Assertions.assertEquals(categoryDtoResult.getCategoryName(), categoryDto.getCategoryName());
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
                () -> Assertions.assertEquals(0, result.get(0).getBoardCount()),
                () -> Assertions.assertEquals(categoryDto2.getCategoryName(), result.get(1).getCategoryName()),
                () -> Assertions.assertEquals(0, result.get(1).getBoardCount()),
                () -> Assertions.assertEquals(categoryDto3.getCategoryName(), result.get(2).getCategoryName()),
                () -> Assertions.assertEquals(0, result.get(2).getBoardCount())
        );
    }

    @Test
    @DisplayName("카테고리 아이디로 카테고리가 존재하는지 확인 - 있는경우")
    void IS_EXIST_CATEGORY_TEST(){
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        categoryRepository.createCategory(categoryDto);
        Integer categoryId = categoryRepository.inquiryAllCategory().get(0).getId();

        Assertions.assertTrue(categoryRepository.isExistCategory(categoryId));

    }

    @Test
    @DisplayName("카테고리 아이디로 카테고리가 존재하는지 확인 - 있는경우")
    void IS_NOT_EXIST_CATEGORY_TEST(){
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        categoryRepository.createCategory(categoryDto);

        Assertions.assertFalse(categoryRepository.isExistCategory(-1));

    }

    @Test
    @DisplayName("카테고리 수정 성공 테스트")
    void MODIFY_CATEGORY_TEST(){
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        categoryRepository.createCategory(categoryDto);

        CategoryDto foundCategoryDto = categoryRepository.inquiryAllCategory().get(0);
        foundCategoryDto.setCategoryName("modified");
        categoryRepository.modifyCategory(foundCategoryDto);

        CategoryDto result = categoryRepository.inquiryCategoryById(foundCategoryDto.getId());

        Assertions.assertAll(
                () -> Assertions.assertEquals(foundCategoryDto.getId(), result.getId()),
                () -> Assertions.assertEquals("modified", result.getCategoryName())
        );
    }

    @Test
    @DisplayName("카테고리 삭제 성공 테스트 - 속한 보드도 모두 삭제")
    void DELETE_CATEGORY_SUCCESS_TEST(){
        //given
        Board board = setDefaultBoard();

        //when
        categoryRepository.deleteCategory(board.getCategory().getId());

        Assertions.assertAll(
                () -> Assertions.assertFalse(categoryRepository.isExistCategory(board.getCategory().getId())),
                () -> Assertions.assertFalse(jpaBoardRepository.existsById(board.getId()))
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

    @Test
    @DisplayName("카테고리 이름 중복체크 테스트")
    void IS_DUPLICATE_CATEGORY_NAME_TEST(){
        String categoryName = "catecate";
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName(categoryName)
                .build();
        categoryRepository.createCategory(categoryDto);
        String categoryName2 = "catecate";

        Assertions.assertTrue(categoryRepository.isDuplicateCategoryName(categoryName2));

    }

}
