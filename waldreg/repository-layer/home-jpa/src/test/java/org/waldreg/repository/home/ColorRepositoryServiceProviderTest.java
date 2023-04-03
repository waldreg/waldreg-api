package org.waldreg.repository.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.waldreg.domain.home.ApplicationColor;
import org.waldreg.home.color.dto.ColorDto;
import org.waldreg.repository.home.repository.ColorJpaRepository;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes = {
        ColorRepositoryServiceProvider.class,
        JpaHomeTestInitializer.class
})
@TestPropertySource("classpath:h2-application.properties")
class ColorRepositoryServiceProviderTest{

    @Autowired
    private ColorRepositoryServiceProvider colorServiceProvider;

    @Autowired
    private ColorJpaRepository colorJpaRepository;

    @Test
    @DisplayName("색깔 수정 및 조회 성공 테스트")
    void UPDATE_COLOR_SUCCESS_TEST(){
        // given
        ColorDto colorDto = ColorDto.builder()
                .primaryColor("#123456")
                .backgroundColor("#123456")
                .build();

        // when
        colorJpaRepository.saveAndFlush(ApplicationColor.builder()
                .primaryColor("#654321")
                .backgroundColor("#654321")
                .build());
        colorServiceProvider.updateColor(colorDto);

        ColorDto result = colorServiceProvider.getColor();

        // then
        assertAll(
                () -> assertEquals("#123456", result.getPrimaryColor()),
                () -> assertEquals("#123456", result.getBackgroundColor())
        );
    }

}
