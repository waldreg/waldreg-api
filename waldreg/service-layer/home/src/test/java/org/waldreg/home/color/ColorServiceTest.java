package org.waldreg.home.color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.home.service.color.management.ColorManager;
import org.waldreg.home.service.color.dto.ColorDto;
import org.waldreg.home.service.color.management.DefaultColorManager;
import org.waldreg.home.service.spi.ColorRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultColorManager.class})
public class ColorServiceTest{

    @Autowired
    ColorManager colorManager;

    @MockBean
    ColorRepository colorRepository;

    @Test
    @DisplayName("애플리케이션 색깔 조회 성공 테스트")
    public void READ_APPLICATION_COLOR_SUCCESS_TEST(){
        //given
        String primaryColor = "FF0000";
        String backgroundColor = "#FFFFFF";
        ColorDto colorDto = ColorDto.builder()
                .primaryColor(primaryColor)
                .backgroundColor(backgroundColor)
                .build();

        //when
        Mockito.when(colorRepository.getColor()).thenReturn(colorDto);
        ColorDto result = colorManager.getColor();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getPrimaryColor(),primaryColor),
                () -> Assertions.assertEquals(result.getBackgroundColor(),backgroundColor)
        );


    }

}
