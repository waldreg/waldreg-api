package org.waldreg.repository.board.repository;

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
import org.waldreg.domain.board.category.Category;

@DataJpaTest
@TestPropertySource("classpath:h2-application.properties")
public class JpaCategoryRepositoryTest{

    @Autowired
    private JpaCategoryRepository jpaCategoryRepository;
    @Autowired
    private JpaUserRepository jpaUserRepository;
    @Autowired
    private JpaCharacterRepository jpaCharacterRepository;

    @Autowired
    JpaCommentRepository jpaCommentRepository;

    @Autowired
    private JpaBoardRepository jpaBoardRepository;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @AfterEach
    public void INIT_BOARD(){
        jpaCommentRepository.deleteAll();
        jpaBoardRepository.deleteAll();
        jpaUserRepository.deleteAll();
        jpaCategoryRepository.deleteAll();
        jpaCharacterRepository.deleteAll();
    }

    @Test
    @DisplayName("카테고리 생성 테스트")
    void CREATE_CATEGORY_SUCCESS_TEST(){
        Category category = Category.builder()
                .categoryName("cate1")
                .build();

        jpaCategoryRepository.saveAndFlush(category);
        entityManager.clear();
        Category foundCategory = jpaCategoryRepository.findAll().get(0);
        Assertions.assertEquals(category.getCategoryName(), foundCategory.getCategoryName());

    }

    @Test
    @DisplayName("카테고리 전체 조회 테스트")
    void INQUIRY_ALL_CATEGORY_SUCCESS_TEST(){
        Category category = Category.builder()
                .categoryName("cate1")
                .build();
        jpaCategoryRepository.saveAndFlush(category);

        Category category2 = Category.builder()
                .categoryName("cate2")
                .build();
        jpaCategoryRepository.saveAndFlush(category2);

        Category category3 = Category.builder()
                .categoryName("cate3")
                .build();
        jpaCategoryRepository.saveAndFlush(category3);
        entityManager.clear();

        List<Category> foundCategoryList = jpaCategoryRepository.findAll();

        Assertions.assertAll(
                ()->Assertions.assertEquals("cate1",foundCategoryList.get(0).getCategoryName()),
                ()->Assertions.assertEquals("cate2",foundCategoryList.get(1).getCategoryName()),
                ()->Assertions.assertEquals("cate3",foundCategoryList.get(2).getCategoryName())
        );

    }

    @Test
    @DisplayName("카테고리 수정 테스트")
    void UPDATE_CATEGORY_SUCCESS_TEST(){
        Category category = Category.builder()
                .categoryName("cate1")
                .build();

        jpaCategoryRepository.saveAndFlush(category);
        entityManager.clear();
        Category foundCategory = jpaCategoryRepository.findAll().get(0);

        foundCategory.setCategoryName("modify");
        entityManager.flush();
        entityManager.clear();
        Category result = jpaCategoryRepository.findById(foundCategory.getId()).get();

        Assertions.assertEquals("modify", result.getCategoryName());
    }

    @Test
    @DisplayName("카테고리 삭제 테스트")
    void DELETE_CATEGORY_SUCCESS_TEST(){
        Category category = Category.builder()
                .categoryName("cate1")
                .build();

        jpaCategoryRepository.saveAndFlush(category);
        entityManager.clear();
        Category foundCategory = jpaCategoryRepository.findAll().get(0);

        jpaCategoryRepository.deleteById(foundCategory.getId());
        entityManager.flush();
        entityManager.clear();

        Assertions.assertFalse(jpaCategoryRepository.existsById(foundCategory.getId()));
    }

}
