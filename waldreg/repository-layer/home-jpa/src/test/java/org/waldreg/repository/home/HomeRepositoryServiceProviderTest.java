package org.waldreg.repository.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.home.HomeContent;
import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;
import org.waldreg.repository.home.repository.HomeJpaRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {
        JpaHomeTestInitializer.class,
        HomeRepositoryServiceProvider.class
})
@TestPropertySource("classpath:h2-application.properties")
class HomeRepositoryServiceProviderTest{

    @Autowired
    private HomeRepositoryServiceProvider homeServiceProvider;

    @Autowired
    private HomeJpaRepository homeJpaRepository;

    @Test
    @DisplayName("홈 게시글 조회 성공 테스트")
    void READ_HOME_CONTENT_SUCCESS_TEST(){
        // given
        String content = "hello world";
        HomeContent homeContent = HomeContent.builder()
                .content(content)
                .build();

        // when
        homeJpaRepository.saveAndFlush(homeContent);
        HomeReadable result = homeServiceProvider.getHome();

        // then
        assertEquals(content, result.getContent());
    }

    @Test
    @DisplayName("홈 게시글 수정 성공 테스트")
    void UPDATE_HOME_CONTENT_SUCCESS_TEST(){
        // given
        String content = "hello world";
        HomeContent homeContent = HomeContent.builder()
                .content(content)
                .build();

        String update = "changed";

        // when
        homeJpaRepository.saveAndFlush(homeContent);
        HomeReadable before = homeServiceProvider.getHome();

        homeServiceProvider.updateHome(() -> update);
        HomeReadable after = homeServiceProvider.getHome();

        // then
        assertAll(
                () -> assertNotEquals(before.getContent(), after.getContent()),
                () -> assertEquals(content, before.getContent()),
                () -> assertEquals(update, after.getContent())
        );
    }

}
