package org.waldreg.home.home;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.home.homecontent.dto.HomeContentDto;
import org.waldreg.home.homecontent.management.DefaultHomeManager;
import org.waldreg.home.homecontent.management.HomeManager;
import org.waldreg.home.spi.HomeRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultHomeManager.class})
public class HomeServiceTest{

    @Autowired
    HomeManager homeManager;

    @MockBean
    HomeRepository homeRepository;

    @Test
    @DisplayName("홈화면 게시글 조회 성공 테스트")
    public void READ_HOME_CONTENT_SUCCESS_TEST(){
        //given
        String content = "This is content for Home~!";
        HomeContentDto homeContentDto = HomeContentDto.builder()
                .content(content)
                .build();

        //when
        Mockito.when(homeRepository.getHome()).thenReturn(homeContentDto);
        HomeContentDto result = homeManager.getHome();

        //then
        Assertions.assertEquals(result.getContent(), homeContentDto.getContent());

    }

    @Test
    @DisplayName("홈화면 게시글 수정 성공 테스트")
    public void UPDATE_HOME_CONTENT_SUCCESS_TEST(){
        //given
        String updateContent = "This is new content of Home~!";
        HomeContentDto homeContentDto = HomeContentDto.builder()
                .content(updateContent)
                .build();

        //when
        homeManager.updateHome(homeContentDto);
        Mockito.when(homeRepository.getHome()).thenReturn(homeContentDto);
        HomeContentDto result = homeManager.getHome();

        //then
        Assertions.assertEquals(result.getContent(), homeContentDto.getContent());

    }

}
